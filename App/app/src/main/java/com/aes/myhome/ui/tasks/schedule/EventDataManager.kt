package com.aes.myhome.ui.tasks.schedule

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.aes.myhome.DIHandler
import com.aes.myhome.DateTimePicker
import com.aes.myhome.storage.database.entities.ScheduleEvent
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

class EventDataManager(
    categoriesEntries: Array<out String>,
    categorySelect: AutoCompleteTextView,
    private val getContext: () -> Context,
    private val categoriesValues: Array<out String>,
    private val dataEdit: TextInputEditText,
    private val dateSelect: AutoCompleteTextView,
    private val enableNotification: CheckBox,
    private val notificationDateTimeText: TextView
) : DatePickerDialog.OnDateSetListener, DateTimePicker.OnDateTimePickListener {

    private var _selectedCategory = ""

    init {
        dateSelect.setOnClickListener {
            val calendar = Calendar.getInstance()

            val picker = DatePickerDialog(
                getContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            picker.show()
        }

        _selectedCategory = categoriesValues[0]

        val adapter =
            ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categoriesEntries)

        categorySelect.setAdapter(adapter)
        categorySelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                _selectedCategory = categoriesValues[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                _selectedCategory = categoriesValues[0]
            }

        }

        val dialog = DateTimePicker(getContext, this)

        enableNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dialog.pick()
                notificationDateTimeText.visibility = View.VISIBLE
            } else {
                notificationDateTimeText.text = ""
                notificationDateTimeText.visibility = View.INVISIBLE
            }
        }
    }

    fun createEvent(): ScheduleEvent? {
        if (dataEdit.text.toString().isBlank()) {
            dataEdit.setError(
                DIHandler.getResources().getString(com.aes.myhome.R.string.error_empty_name), // TODO
                AppCompatResources.getDrawable(getContext(), com.aes.myhome.R.drawable.error)
            )
            dataEdit.requestFocus()
            return null
        }

        if (dateSelect.text.toString().isBlank()) {
            dateSelect.setError(
                DIHandler.getResources().getString(com.aes.myhome.R.string.error_empty_useByDate), // TODO
                AppCompatResources.getDrawable(getContext(), com.aes.myhome.R.drawable.error)
            )
            dateSelect.requestFocus()
            return null
        }

        return ScheduleEvent(
            dateSelect.text.toString(),
            dataEdit.text.toString(),
            _selectedCategory,
            enableNotification.isChecked,
            if (notificationDateTimeText.text.isNotBlank())
                notificationDateTimeText.text.toString()
            else null
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = LocalDate.of(year, month + 1, dayOfMonth)
        val str = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(date)
        dateSelect.setText(str)
    }

    override fun onDateTimePicked(day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        val date = LocalDateTime.of(year, month + 1, day, hour, minute)
        val str = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date)
        notificationDateTimeText.text = str
    }

}