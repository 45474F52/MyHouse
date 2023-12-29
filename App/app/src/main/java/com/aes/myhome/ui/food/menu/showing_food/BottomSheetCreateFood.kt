package com.aes.myhome.ui.food.menu.showing_food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.aes.myhome.DateTimePicker
import com.aes.myhome.R
import com.aes.myhome.databinding.BottomSheetCreateFoodBinding
import com.aes.myhome.storage.database.entities.Food
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle

class BottomSheetCreateFood(
    private val receiver: ICallbackReceiver
) : BottomSheetDialogFragment(), DateTimePicker.OnDateTimePickListener {

    interface ICallbackReceiver {
        fun onPostData(food: Food)
    }

    companion object {
        val TAG = BottomSheetCreateFood::class.simpleName
    }

    private var _binding: BottomSheetCreateFoodBinding? = null
    private val binding: BottomSheetCreateFoodBinding
        get() = _binding!!

    private lateinit var _dateTimePicker: DateTimePicker
    private lateinit var _pickDateTimeClicker: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateFoodBinding.inflate(inflater, container, false)

        _dateTimePicker = DateTimePicker({ requireContext() }, this)

        val nameEdit = binding.foodNameEdit
        _pickDateTimeClicker = binding.foodUseByDateEdit
        val quantityEdit = binding.foodQuantityEdit
        val proteinEdit = binding.foodProteinEdit
        val fatEdit = binding.foodFatEdit
        val carbsEdit = binding.foodCarbsEdit
        val caloriesEdit = binding.foodCaloriesEdit
        val descriptionEdit = binding.foodDescriptionEdit

        _pickDateTimeClicker.setOnClickListener {
            _dateTimePicker.pick()
        }

        val addFoodBtn = binding.addFoodBtn
        addFoodBtn.setOnClickListener {
            val name = nameEdit.text.toString()

            if (name.isBlank()) {
                nameEdit.setError(
                    getString(R.string.error_empty_name),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                nameEdit.requestFocus()
                return@setOnClickListener
            }

            val useByDate = _pickDateTimeClicker.text.toString()
            if (useByDate.isLocalDateTime().not()) {
                _pickDateTimeClicker.setError(
                    getString(R.string.error_empty_useByDate),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error)
                )
                _pickDateTimeClicker.requestFocus()
                return@setOnClickListener
            }

            val description = descriptionEdit.text.toString()

            var protein = .0
            var fat = .0
            var carbs = .0
            var calories = .0

            if (!tryParseDoubles(
                    EditorWrapper(proteinEdit) { protein = it },
                    EditorWrapper(fatEdit) { fat = it },
                    EditorWrapper(carbsEdit) { carbs = it },
                    EditorWrapper(caloriesEdit) { calories = it }
                )) {
                return@setOnClickListener
            }

            val quantity: Byte

            try {
                quantity = quantityEdit.text.toString().toByte()
            }
            catch (_: NumberFormatException) {
                quantityEdit.setError(
                    getString(R.string.error_numberFormat),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error)
                )
                quantityEdit.requestFocus()
                return@setOnClickListener
            }

            nameEdit.text.clear()
            descriptionEdit.text.clear()
            _pickDateTimeClicker.text = ""
            proteinEdit.text.clear()
            fatEdit.text.clear()
            carbsEdit.text.clear()
            caloriesEdit.text.clear()
            quantityEdit.text.clear()

            val food = Food(name, useByDate, description, protein, fat, carbs, calories, quantity)
            receiver.onPostData(food)

            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.foodNameEdit.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateTimePicked(day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        val dateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
        val str = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(dateTime)
        _pickDateTimeClicker.text = str
    }

    private fun String.isLocalDateTime(): Boolean {
        return try {
            LocalDateTime.parse(
                this,
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            true
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            false
        }
    }

    private data class EditorWrapper(val editor: EditText, val onSetValue: (Double) -> Unit)

    private fun tryParseDoubles(vararg wrappers: EditorWrapper): Boolean {
        for (wrapper in wrappers) {
            val number = wrapper.editor.text.toString().toDoubleOrNull()

            if (number != null) {
                wrapper.onSetValue(number)
            } else {
                wrapper.editor.setError(
                    getString(R.string.error_numberFormat),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error)
                )
                wrapper.editor.requestFocus()
                return false
            }
        }

        return true
    }
}