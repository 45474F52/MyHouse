package com.aes.myhome.ui.food.menu

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.aes.myhome.DIHandler
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.Food
import java.io.Serializable
import java.util.Calendar
import java.util.Locale

class EditFoodDialogFragment : DialogFragment(), OnDateSetListener, OnTimeSetListener {

    private lateinit var _food: Food

    private lateinit var _receiver: ICallbackReceiver

    private lateinit var _useByDateView: TextView

    interface ICallbackReceiver : Serializable {
        fun onPositive(food: Food)
        fun onNegative(food: Food)
    }

    companion object {
        private const val RECEIVER_KEY = "receiver_key"

        val TAG = EditFoodDialogFragment::class.simpleName

        fun getInstance(receiver: ICallbackReceiver): EditFoodDialogFragment {
            return Bundle().apply {
                putSerializable(RECEIVER_KEY, receiver)
            }.let {
                EditFoodDialogFragment().apply { arguments = it }
            }
        }
    }

    fun setFood(food: Food) {
        _food = food
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _receiver = arguments?.getSerializable(RECEIVER_KEY) as ICallbackReceiver
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val foodView: View = inflater.inflate(R.layout.dialog_food, null)

            val foodNameView: TextView = foodView.findViewById(R.id.food_name_edit)
            _useByDateView = foodView.findViewById(R.id.food_useByDate_text)
            val descriptionView: TextView = foodView.findViewById(R.id.food_description_edit)
            val proteinView: TextView = foodView.findViewById(R.id.food_protein_edit)
            val fatView: TextView = foodView.findViewById(R.id.food_fat_edit)
            val carbsView: TextView = foodView.findViewById(R.id.food_carbs_edit)
            val caloriesView: TextView = foodView.findViewById(R.id.food_calories_edit)
            val quantityView: TextView = foodView.findViewById(R.id.food_quantity_edit)

            foodNameView.text = _food.foodName
            _useByDateView.text = DIHandler.getResources().getString(R.string.food_format_useByDate, _food.useByDate)
            descriptionView.text = _food.description
            proteinView.text = DIHandler.getResources().getString(R.string.food_format_protein, _food.protein)
            fatView.text = DIHandler.getResources().getString(R.string.food_format_fat, _food.fat)
            carbsView.text = DIHandler.getResources().getString(R.string.food_format_carbs, _food.carbs)
            caloriesView.text = DIHandler.getResources().getString(R.string.food_format_calories, _food.calories)
            quantityView.text = DIHandler.getResources().getString(R.string.food_format_quantity, _food.quantity)

            _useByDateView.setOnClickListener {
                setCurrentDateTime()
                DatePickerDialog(requireContext(), this, _year, _month, _day).show()
            }

            builder
                .setView(foodView)
                .setMessage("Отредактируйте нужные поля ниже")
                .setPositiveButton("Сохранить") { _, _ ->
                    try {
                        _food.foodName = foodNameView.text.toString()
                        _food.useByDate = _useByDateView.text.toString()
                        _food.description = descriptionView.text.toString()
                        _food.protein = proteinView.text.toString().toDouble()
                        _food.fat = fatView.text.toString().toDouble()
                        _food.carbs = carbsView.text.toString().toDouble()
                        _food.calories = caloriesView.text.toString().toDouble()
                        _food.quantity = quantityView.text.toString().toByte()
                    }
                    catch (_: NumberFormatException) {

                    }

                    _receiver.onPositive(_food)
                }
                .setPositiveButton("Отменить") { _, _ ->
                    _receiver.onNegative(_food)
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
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

    private fun setCurrentDateTime() {
        val calendar = Calendar.getInstance(Locale.forLanguageTag("ru-RU"))

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

        setCurrentDateTime()

        TimePickerDialog(requireContext(), this, _hour, _minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        _savedHour = hourOfDay
        _savedMinute = minute

        _useByDateView.text = getString(R.string.food_format_date_edit, _savedDay, _savedMonth, _savedYear, _savedHour, _savedMinute)
    }
}