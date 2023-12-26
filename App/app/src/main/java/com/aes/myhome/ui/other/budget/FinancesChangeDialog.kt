package com.aes.myhome.ui.other.budget

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aes.myhome.R
import com.aes.myhome.objects.finances.UserFinances
import java.time.LocalDate
import java.util.Calendar

class FinancesChangeDialog(
    private val receiver: ICallbackReceiver,
    private val isRevenue: Boolean
) : DialogFragment(), OnDateSetListener {

    companion object {
        val TAG = FinancesChangeDialog::class.simpleName
    }

    interface ICallbackReceiver {
        fun onPostFinanceData(summa: Double, date: String)
        fun onNumberFormatExceptionOccurred()
        fun onDateNotSet()
    }

    private lateinit var _dateView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val dialogView: View = inflater.inflate(R.layout.dialog_finances_change, null)

            val summaView: EditText = dialogView.findViewById(R.id.finances_summa_et)

            _dateView = dialogView.findViewById(R.id.finances_date_tv)
            _dateView.setOnClickListener {
                val calendar = Calendar.getInstance()

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(
                    requireContext(),
                    this,
                    year,
                    month,
                    day
                ).also { dialog ->
                    val minDate = Calendar.getInstance()
                    minDate.set(Calendar.DAY_OF_MONTH, 1)
                    dialog.datePicker.minDate = minDate.timeInMillis

                    val maxDate = Calendar.getInstance()
                    maxDate.set(Calendar.DAY_OF_MONTH, day)
                    dialog.datePicker.maxDate = maxDate.timeInMillis
                }.show()
            }

            var summa: Double
            var date: String

            val title: String
            val message: String

            if (isRevenue) {
                title = getString(R.string.dialog_finance_title_revenue)
                message = getString(R.string.dialog_finance_message_revenue)
            }
            else {
                title = getString(R.string.dialog_finance_title_expense)
                message = getString(R.string.dialog_finance_message_expense)
            }

            builder
                .setView(dialogView)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.action_apply)) { _, _ ->
                    date = _dateView.text.toString()

                    if (date.isBlank()) {
                        receiver.onDateNotSet()
                    }

                    try {
                        summa = summaView.text.toString().toDouble()
                        if (summa < .0) {
                            receiver.onNumberFormatExceptionOccurred()
                            return@setPositiveButton
                        }
                    }
                    catch (_: NumberFormatException) {
                        receiver.onNumberFormatExceptionOccurred()
                        return@setPositiveButton
                    }

                    receiver.onPostFinanceData(summa, date)
                }
                .setNegativeButton(getString(R.string.action_cancel)) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val ld = LocalDate.of(year, month + 1, dayOfMonth)
        val str = UserFinances.dateFormatter.format(ld)

        _dateView.text = str
    }
}