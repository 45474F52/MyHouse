package com.aes.myhome.ui.other.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aes.myhome.R
import com.aes.myhome.databinding.FragmentBudgetBinding
import com.aes.myhome.objects.finances.FinanceUnit
import com.aes.myhome.objects.finances.UserFinances
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAnimationType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.absoluteValue

@AndroidEntryPoint
class BudgetFragment : Fragment(), FinancesChangeDialog.ICallbackReceiver {

    private enum class StatisticsType {
        FOR_WEEK,
        FOR_MONTH
    }

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: BudgetViewModel

    private lateinit var _userFinances: UserFinances
    private lateinit var _budgetSumma: TextView
    private lateinit var _budgetStatus: TextView
    private lateinit var _statisticsViewBtn: Button
    private lateinit var _statisticsChart: AAChartView

    private var _dialogTypeIsRevenue = true
    private var _hasError = false

    private var _statisticsType = StatisticsType.FOR_WEEK

    private val _monthFormatter = DateTimeFormatter.ofPattern("MMMM dd")
    private val _weekFormatter = DateTimeFormatter.ofPattern("EEEE")

    private var _hasWeekChanges = true
    private var _hasMonthChanges = true

    private val _weekModel = AAChartModel()
        .chartType(AAChartType.Column)
        .animationType(AAChartAnimationType.SwingTo)
        .dataLabelsEnabled(false)
        .legendEnabled(true)
        .yAxisTitle("")
        .yAxisAllowDecimals(true)

    private val _monthModel = AAChartModel()
        .chartType(AAChartType.Column)
        .animationType(AAChartAnimationType.SwingTo)
        .dataLabelsEnabled(false)
        .legendEnabled(true)
        .yAxisTitle("")
        .yAxisAllowDecimals(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        _viewModel.userFinances.observe(viewLifecycleOwner) {
            _userFinances = it
            updateBudgetView()
            updateBudgetStatusView()
            updateStatisticsChart()
        }

        _budgetSumma = binding.budgetSummaTv
        _budgetStatus = binding.budgetStatusTv

        _statisticsChart = binding.statisticsChart
        _statisticsChart.isClearBackgroundColor = true

        val revenueBtn = binding.revenueBtn
        revenueBtn.setOnClickListener {
            val dialog = FinancesChangeDialog(this, true)
            _dialogTypeIsRevenue = true
            dialog.show(requireActivity().supportFragmentManager, FinancesChangeDialog.TAG)
        }

        val expenseBtn = binding.expenseBtn
        expenseBtn.setOnClickListener {
            val dialog = FinancesChangeDialog(this, false)
            _dialogTypeIsRevenue = false
            dialog.show(requireActivity().supportFragmentManager, FinancesChangeDialog.TAG)
        }

        _statisticsViewBtn = binding.statisticsViewBtn
        _statisticsViewBtn.setOnClickListener {
            updateStatisticsChart()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPostFinanceData(summa: Double, date: String) {
        if (_hasError.not()) {
            if (_dialogTypeIsRevenue) {
                val revenues = FinanceUnit(summa, date)
                _userFinances.addRevenueRecord(revenues)
            }
            else {
                val expenses = FinanceUnit(summa, date)
                _userFinances.addExpenseRecord(expenses)
            }

            updateBudgetView()

            val d = date.toLocalDate()
            if (d.withinCurrentMonth()) {
                _hasMonthChanges = true

                _hasWeekChanges = d.withinCurrentWeek()

                updateBudgetStatusView()
            } else {
                _hasMonthChanges = false
            }

            _viewModel.saveData()

            updateStatisticsChart()
        }
        else {
            _hasError = false
        }
    }

    private fun updateBudgetStatusView() {
        val currentDayBudget = _userFinances.budget
        val previousDayBudget = _userFinances.getBudget(LocalDate.now().minusDays(1L))
        val percentChange = if (currentDayBudget == .0 && previousDayBudget == .0) {
            .0
        } else if (previousDayBudget != .0) {
            ((currentDayBudget - previousDayBudget) / previousDayBudget * 100)
        } else {
            currentDayBudget
        }

        val drawable = if (percentChange < 0) {
            ContextCompat.getDrawable(requireContext(), R.drawable.arrow_downward)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.arrow_upward)
        }

        _budgetStatus.text = getString(R.string.budget_format_percent, percentChange.absoluteValue)
        _budgetStatus.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            drawable,
            null)
    }

    override fun onNumberFormatExceptionOccurred() {
        _hasError = true
        createSnackBar(getString(R.string.budget_error_summa))
    }

    override fun onDateNotSet() {
        _hasError = true
        createSnackBar(getString(R.string.budget_error_date))
    }

    private fun createSnackBar(message: String) {
        Snackbar
            .make(requireContext(), requireView(), message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun updateBudgetView() {
        val numberFormat = NumberFormat.getCurrencyInstance()
        val str = numberFormat
            .format(_userFinances.budget)
            .replace(numberFormat.currency!!.symbol, "") + numberFormat.currency!!.symbol

        _budgetSumma.text = str
    }

    private fun getCategories(formatter: DateTimeFormatter): Array<String> {
        return _seriesDates.map { formatter.format(it) }.toTypedArray()
    }

    private fun updateStatisticsChart() {
        if (_statisticsType == StatisticsType.FOR_WEEK) {
            _statisticsType = StatisticsType.FOR_MONTH
            _statisticsViewBtn.text = getString(R.string.budget_statistics_forMonth)

            if (_hasMonthChanges) {
                val startDate = LocalDate.now().withDayOfMonth(1)
                val endDate = startDate.plusMonths(1).minusDays(1)

                _monthModel
                    .series(getSeries(startDate, endDate, 30))
                    .categories(getCategories(_monthFormatter))

                _hasMonthChanges = false
            }

            _statisticsChart.aa_drawChartWithChartModel(_monthModel)
        } else {
            _statisticsType = StatisticsType.FOR_WEEK
            _statisticsViewBtn.text = getString(R.string.budget_statistics_forWeek)

            if (_hasWeekChanges) {
                val now = ZonedDateTime.now()
                val dayOfWeek = WeekFields.of(Locale.getDefault()).dayOfWeek()
                val startDate = now.with(dayOfWeek, 1).with(LocalTime.MIN).toLocalDate()
                val endDate = now.with(dayOfWeek, 7).with(LocalTime.MAX).toLocalDate()

                _weekModel
                    .series(getSeries(startDate, endDate, 7))
                    .categories(getCategories(_weekFormatter))

                _hasWeekChanges = false
            }

            _statisticsChart.aa_drawChartWithChartModel(_weekModel)
        }
    }

    private val _seriesDates = mutableListOf<LocalDate>()
    private fun getSeries(startDate: LocalDate, endDate: LocalDate, limit: Int ): Array<Any> {
        val revenues = selectFinanceUnits(
            _userFinances.revenues,
            startDate, endDate, limit
        ).reversed()

        val expenses = selectFinanceUnits(
            _userFinances.expenses,
            startDate, endDate, limit
        ).reversed()

        val (revenuesList, expensesList) = supplementLists(revenues, expenses, getDatesBetween(startDate, endDate))

        _seriesDates.clear()
        _seriesDates.addAll(revenuesList.map { it.date.toLocalDate() })

        val revenuesSeries = AASeriesElement()
            .name(getString(R.string.budget_category_revenues))
            .color("#00FF00")
            .data(revenuesList.map { it.money }.toTypedArray())

        val expensesSeries = AASeriesElement()
            .name(getString(R.string.budget_category_expenses))
            .color("#FF0000")
            .data(expensesList.map { it.money }.toTypedArray())

        return arrayOf(revenuesSeries, expensesSeries)
    }

    private fun supplementLists(
        firstList: List<FinanceUnit>,
        secondList: List<FinanceUnit>,
        dates: List<LocalDate>
    ): Pair<List<FinanceUnit>, List<FinanceUnit>> {

        val firstMap = firstList.associateBy { it.date.toLocalDate() }
        val secondMap = secondList.associateBy { it.date.toLocalDate() }

        val firstResult = ArrayList<FinanceUnit>()
        val secondResult = ArrayList<FinanceUnit>()

        for (date in dates) {
            val firstUnit = firstMap[date]
            val secondUnit = secondMap[date]

            if (firstUnit == null && secondUnit == null) {
                continue
            }

            if (secondUnit == null) {
                firstResult.add(firstUnit!!)
                secondResult.add(FinanceUnit(.0, firstUnit.date))
            } else if (firstUnit == null) {
                secondResult.add(secondUnit)
                firstResult.add(FinanceUnit(.0, secondUnit.date))
            } else {
                firstResult.add(firstUnit)
                secondResult.add(secondUnit)
            }
        }

        return firstResult to secondResult
    }

    private fun getDatesBetween(from: LocalDate, to: LocalDate): List<LocalDate> {
        val result = mutableListOf<LocalDate>()
        var current = from
        while (!current.isAfter(to)) {
            result.add(current)
            current = current.plusDays(1)
        }
        return result
    }

    private fun selectFinanceUnits(
        list: Collection<FinanceUnit>,
        startDate: LocalDate,
        endDate: LocalDate,
        limit: Int
    ): List<FinanceUnit> {
        return list
            .filter { it.date.toLocalDate().between(startDate, endDate, true) }
            .groupBy { it.date }
            .map { (date, group) -> FinanceUnit(group.sumOf { it.money }, date) }
            .take(limit)
    }

    private fun String.toLocalDate(): LocalDate {
        return LocalDate.parse(this, UserFinances.dateFormatter)
    }

    private fun LocalDate.between(start: LocalDate, end: LocalDate, inclusive: Boolean): Boolean {
        return if (inclusive) {
            (this.isAfter(start) || this.isEqual(start))
                    && (this.isBefore(end) || this.isEqual(end))
        } else {
            this.isAfter(start) && this.isBefore(end)
        }
    }

    private fun LocalDate.withinCurrentWeek(): Boolean {
        val currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        val weekOfYear = this.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        return currentWeek == weekOfYear
    }

    private fun LocalDate.withinCurrentMonth(): Boolean {
        val currentDate = LocalDate.now()
        val startOfMonth = currentDate.withDayOfMonth(1)
        val endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())
        return this.between(startOfMonth, endOfMonth, true)
    }
}