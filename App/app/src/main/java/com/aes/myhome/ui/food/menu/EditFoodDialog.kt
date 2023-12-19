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
import com.aes.myhome.DateTimePicker
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.Food
import java.io.Serializable
import java.util.Calendar
import java.util.Locale

class EditFoodDialog : DialogFragment(), DateTimePicker.OnDateTimePickListener {

    private lateinit var _food: Food

    private lateinit var _receiver: ICallbackReceiver

    private lateinit var _useByDateView: TextView

    private lateinit var _dateTimePicker: DateTimePicker

    interface ICallbackReceiver : Serializable {
        fun onPositive(food: Food)
    }

    companion object {
        private const val RECEIVER_KEY = "receiver_key"

        val TAG = EditFoodDialog::class.simpleName

        fun getInstance(receiver: ICallbackReceiver): EditFoodDialog {
            return Bundle().apply {
                putSerializable(RECEIVER_KEY, receiver)
            }.let {
                EditFoodDialog().apply { arguments = it }
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
        _dateTimePicker = DateTimePicker({ requireContext() }, this)

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
            _useByDateView.text = getString(R.string.food_format_useByDate, _food.useByDate)
            descriptionView.text = _food.description
            proteinView.text = getString(R.string.food_format_protein, _food.protein)
            fatView.text = getString(R.string.food_format_fat, _food.fat)
            carbsView.text = getString(R.string.food_format_carbs, _food.carbs)
            caloriesView.text = getString(R.string.food_format_calories, _food.calories)
            quantityView.text = getString(R.string.food_format_quantity, _food.quantity)

            _useByDateView.setOnClickListener {
                _dateTimePicker.pick()
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

                    if (_food.foodName.isNotBlank() && _food.useByDate.isNotBlank())
                        _receiver.onPositive(_food)
                }
                .setNegativeButton("Отменить") { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDateTimePicked(day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        _useByDateView.text =
            getString(R.string.food_format_date_edit, day, month, year, hour, minute)
    }
}