package com.aes.myhome.objects.finances

import com.aes.myhome.Queue
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Serializable
class UserFinances {
    companion object {
        const val RECORDS_LIMIT = 30
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    }

    private var _budget = .0
    private val _revenues = Queue<FinanceUnit>()
    private val _expenses = Queue<FinanceUnit>()

    fun addRevenueRecord(revenues: FinanceUnit) {
        _budget += revenues.money

        _revenues.offer(revenues)

        if (_revenues.size > RECORDS_LIMIT) {
            _revenues.poll()
        }
    }

    fun addExpenseRecord(expenses: FinanceUnit) {
        _budget -= expenses.money

        _expenses.offer(expenses)

        if (_expenses.size > RECORDS_LIMIT) {
            _expenses.poll()
        }
    }

    fun getBudget(date: LocalDate): Double {

        val newDate = date.plusDays(1L)

        val filteredRevenues = revenues.filter {
            newDate.isAfter(LocalDate.parse(it.date, dateFormatter))
        }

        val filteredExpenses = expenses.filter {
            newDate.isAfter(LocalDate.parse(it.date, dateFormatter))
        }

        val sum1 = filteredRevenues.sumOf { it.money }
        val sum2 = filteredExpenses.sumOf { it.money }

        return sum1 - sum2
    }

    val budget: Double
        get() = _budget

    val revenues: Collection<FinanceUnit>
        get() = _revenues

    val expenses: Collection<FinanceUnit>
        get () = _expenses
}

@Serializable
data class FinanceUnit(val money: Double, val date: String)
