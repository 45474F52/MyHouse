package com.aes.myhome.ui.food.menu

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aes.myhome.DateTimePicker
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.Food
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

    @Suppress("DEPRECATION")
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
            // TODO: Input calories from View
            val caloriesView: TextView = foodView.findViewById(R.id.food_calories_edit)
            val quantityView: TextView = foodView.findViewById(R.id.food_quantity_edit)

            foodNameView.text = _food.foodName
            _useByDateView.text = _food.useByDate
            descriptionView.text = _food.description
            proteinView.text = _food.protein.toString()
            fatView.text = _food.fat.toString()
            carbsView.text = _food.carbs.toString()
            caloriesView.text = _food.calories.toString()
            quantityView.text = _food.quantity.toString()

            _useByDateView.setOnClickListener {
                _dateTimePicker.pick()
            }

            builder
                .setView(foodView)
                .setMessage(getString(R.string.dialog_editFood_message))
                .setPositiveButton(getString(R.string.action_save)) { _, _ ->
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
                .setNegativeButton(getString(R.string.action_cancel)) { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDateTimePicked(day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        val date = LocalDateTime.of(year, month + 1, day, hour, minute)
        val str = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date)
        _useByDateView.text = str
    }
}