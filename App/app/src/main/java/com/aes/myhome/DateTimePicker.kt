package com.aes.myhome

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.Calendar
import java.util.Locale

class DateTimePicker(
    private val getContext: () -> Context,
    private val listener: OnDateTimePickListener,
    private val locale: Locale = Locale.forLanguageTag("ru-RU"),
    private val is24HourView: Boolean = true)
    : OnDateSetListener, OnTimeSetListener {

    interface OnDateTimePickListener {
        fun onDatePicked(day: Int, month: Int, year: Int)
        fun onTimePicked(hour: Int, minute: Int)
    }

    private var _day = 0
    private var _month = 0
    private var _year = 0
    private var _hour = 0
    private var _minute = 0

    private var _savedDay = 0
    private var _savedMonth = 0
    private var _savedYear = 0
    private var _savedHour = 0
    private var _savedMinute = 0

    fun pick() {
        setCurrentDateTime()
        DatePickerDialog(getContext(), this, _year, _month, _day).show()
    }

    private fun setCurrentDateTime() {
        val calendar = Calendar.getInstance(locale)

        _day = calendar.get(Calendar.DAY_OF_MONTH)
        _month = calendar.get(Calendar.MONTH)
        _year = calendar.get(Calendar.YEAR)
        _hour = calendar.get(Calendar.HOUR)
        _minute = calendar.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        _savedDay = dayOfMonth
        _savedMonth = month
        _savedYear = year

        listener.onDatePicked(_savedDay, _savedMonth, _savedYear)

        setCurrentDateTime()

        TimePickerDialog(getContext(), this, _hour, _minute, is24HourView).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        _savedHour = hourOfDay
        _savedMinute = minute

        listener.onTimePicked(_savedHour, _savedMinute)
    }
}