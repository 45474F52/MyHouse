package com.aes.myhome.ui.food.menu.showing_food

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.DateTimePicker
import com.aes.myhome.IItemClickListener
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.FoodAdapter
import com.aes.myhome.databinding.FragmentMenuFoodBinding
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.ui.food.menu.EditFoodDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import kotlin.NumberFormatException

@AndroidEntryPoint
class MenuFoodFragment : Fragment(),
    IItemClickListener,
    DateTimePicker.OnDateTimePickListener,
    ItemTouchCallback.Receiver<Food>,
    EditFoodDialog.ICallbackReceiver {

    private var _binding: FragmentMenuFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: MenuFoodViewModel
    private lateinit var _dateTimePicker: DateTimePicker
    private lateinit var _adapter: FoodAdapter

    private lateinit var _pickDateTimeClicker: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuFoodBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[MenuFoodViewModel::class.java]

        _dateTimePicker = DateTimePicker({ requireContext() }, this)

        _viewModel.loadFood {
            _adapter = FoodAdapter(_viewModel.food.value!!, this)

            val recycler = binding.root.findViewById<RecyclerView>(R.id.food_list)
            recycler.adapter = _adapter
            recycler.layoutManager = LinearLayoutManager(context)

            ItemTouchHelper(
                ItemTouchCallback(
                    0,
                    swipeDirs = ItemTouchHelper.RIGHT,
                    list = _viewModel.food.value!!,
                    recycler = recycler,
                    this,
                    getString(R.string.action_undo))
            ).attachToRecyclerView(recycler)
        }

        val toolsContainer: LinearLayout = binding.root.findViewById(R.id.food_tools_container)

        val nameTextEditor: EditText = binding.root.findViewById(R.id.food_name_edit)
        val descriptionTextEditor: EditText = binding.root.findViewById(R.id.food_description_edit)
        _pickDateTimeClicker = binding.root.findViewById(R.id.food_useByDate_edit)
        val proteinTextEditor: EditText = binding.root.findViewById(R.id.food_protein_edit)
        val fatTextEditor: EditText = binding.root.findViewById(R.id.food_fat_edit)
        val carbsTextEditor: EditText = binding.root.findViewById(R.id.food_carbs_edit)
        val caloriesTextEditor: EditText = binding.root.findViewById(R.id.food_calories_edit)
        val quantityTextEditor: EditText = binding.root.findViewById(R.id.food_quantity_edit)

        val collapseBtn: Button = binding.root.findViewById(R.id.collapse_food_tools_btn)
        val addFoodBtn: Button = binding.root.findViewById(R.id.add_food_btn)
        val clearListBtn: Button = binding.root.findViewById(R.id.clear_food_list_btn)

        addFoodBtn.setOnClickListener {
            val name = nameTextEditor.text.toString()
            if (name.isBlank()) {
                nameTextEditor.setError(
                    getString(R.string.error_empty_name),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                nameTextEditor.requestFocus()
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

            val description = descriptionTextEditor.text.toString()

            var protein = .0
            var fat = .0
            var carbs = .0
            var calories = .0

            if (!tryParseDoubles(
                EditorWrapper(proteinTextEditor) { protein = it },
                EditorWrapper(fatTextEditor) { fat = it },
                EditorWrapper(carbsTextEditor) { carbs = it },
                EditorWrapper(caloriesTextEditor) { calories = it }
            )) {
                return@setOnClickListener
            }

            val quantity: Byte

            try {
                quantity = quantityTextEditor.text.toString().toByte()
            }
            catch (_: NumberFormatException) {
                quantityTextEditor.setError(
                    getString(R.string.error_numberFormat),
                    AppCompatResources.getDrawable(requireContext(), R.drawable.error)
                )
                quantityTextEditor.requestFocus()
                return@setOnClickListener
            }

            _viewModel.add(Food(
                name,
                useByDate,
                description,
                protein,
                fat,
                carbs,
                calories,
                quantity))

            _adapter.notifyItemInserted(_viewModel.count.value!!.minus(1))

            nameTextEditor.text.clear()
            descriptionTextEditor.text.clear()
            _pickDateTimeClicker.text = ""
            proteinTextEditor.text.clear()
            fatTextEditor.text.clear()
            carbsTextEditor.text.clear()
            caloriesTextEditor.text.clear()
            quantityTextEditor.text.clear()

            toolsContainer.visibility = View.GONE
            collapseBtn.visibility = View.VISIBLE
        }

        _pickDateTimeClicker.setOnClickListener {
            _dateTimePicker.pick()
        }

        collapseBtn.setOnClickListener {
            toolsContainer.visibility = View.VISIBLE
            collapseBtn.visibility = View.GONE
            nameTextEditor.requestFocus()
        }

        clearListBtn.setOnClickListener {
            val size = _viewModel.count.value!!
            _viewModel.clear()
            _adapter.notifyItemRangeRemoved(0, size)
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            clearListBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var _changedItemIndex = -1
    override fun onItemLongClick(index: Int) {
        _changedItemIndex = index

        val item = _viewModel.food.value!![index]

        val dialog = EditFoodDialog.getInstance(this)
        dialog.setFood(item)
        dialog.show(requireActivity().supportFragmentManager, EditFoodDialog.TAG)
    }

    override fun onItemClick(index: Int) { }

    override fun onDateTimePicked(day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
        val str = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dateTime)
        _pickDateTimeClicker.text = str
    }

    override fun onPositive(food: Food) {
        if (_changedItemIndex != -1) {
            _viewModel.update(food)
            _adapter.notifyItemChanged(_changedItemIndex)
            _changedItemIndex = -1
        }
    }

    override fun onDelete(index: Int, item: Food) {
        _viewModel.requestToDelete(index)
        _adapter.notifyItemRemoved(index)
    }

    override fun onUndo(index: Int, item: Food) {
        _viewModel.undoDeletion(index, item)
        _adapter.notifyItemInserted(index)
    }

    override fun onDismissed(item: Food) {
        _viewModel.confirmDeletion(item)
    }

    private fun String.isLocalDateTime(): Boolean {
        return try {
            LocalDateTime.parse(
                this,
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    private data class EditorWrapper(val editor: EditText, val onSetValue: (Double) -> Unit)

    private fun tryParseDoubles(vararg wrappers: EditorWrapper): Boolean {
        for (wrapper in wrappers) {
            try {
                val number = wrapper.editor.text.toString().toDouble()
                wrapper.onSetValue(number)
            }
            catch (_: NumberFormatException) {
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