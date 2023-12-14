package com.aes.myhome.ui.food.menu

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.FoodAdapter
import com.aes.myhome.databinding.FragmentMenuBinding
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.repositories.FoodRepository
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment(),
    View.OnClickListener,
    IItemClickListener,
    EditFoodDialogFragment.ICallbackReceiver,
    OnDateSetListener,
    OnTimeSetListener {

    @Inject lateinit var repository: FoodRepository

    private lateinit var _viewModel: MenuViewModel

    private lateinit var _showFoodListBtn: Button

    private lateinit var _pickDateTimeClicker: TextView
    private lateinit var _toolsContainer: LinearLayout
    private lateinit var _nameTextEditor: EditText
    private lateinit var _descriptionTextEditor: EditText
    private lateinit var _proteinTextEditor: EditText
    private lateinit var _fatTextEditor: EditText
    private lateinit var _carbsTextEditor: EditText
    private lateinit var _caloriesTextEditor: EditText
    private lateinit var _quantityTextEditor: EditText
    private lateinit var _closeFoodPopupButton: ImageButton
    private lateinit var _collapseBtn: Button
    private lateinit var _addFoodBtn: Button
    private lateinit var _clearListBtn: Button

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[MenuViewModel::class.java]

        val adapter = FoodAdapter(_viewModel.foodList, this)

        _showFoodListBtn = binding.root.findViewById(R.id.show_food_list_btn)
        _showFoodListBtn.setOnClickListener {

            val popupView = layoutInflater.inflate(R.layout.popup_food, container, false)
            val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.argb(200, 0, 0, 0)))

            val recycler = popupView.findViewById<RecyclerView>(R.id.food_list)
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(context)

            ItemTouchHelper(
                ItemTouchCallback(
                    0,
                    swipeDirs = ItemTouchHelper.RIGHT,
                    list = _viewModel.foodList,
                    recycler = recycler,
                    adapter = adapter,
                    onDelete = { id: Int, _: Food ->
                        _viewModel.requestToDelete(id)
                        adapter.notifyItemRemoved(id)
                    },
                    onUndo = { _: Int, _: Food ->
                        _viewModel.undoDeletion()
                        //adapter.notifyItemInserted(_currentItemToDelete) // throws exception when index is 0 :(
                        adapter.notifyDataSetChanged()
                    },
                    onDismissed = {
                        _viewModel.confirmDeletion()
                    })
            ).attachToRecyclerView(recycler)

            _viewModel.updateFood()

            adapter.notifyItemRangeInserted(0, _viewModel.foodList.size)

            _toolsContainer = popupView.findViewById(R.id.food_tools_container)

            _nameTextEditor = popupView.findViewById(R.id.food_name_edit)
            _descriptionTextEditor = popupView.findViewById(R.id.food_description_edit)
            _pickDateTimeClicker = popupView.findViewById(R.id.food_useByDate_edit)
            _proteinTextEditor = popupView.findViewById(R.id.food_protein_edit)
            _fatTextEditor = popupView.findViewById(R.id.food_fat_edit)
            _carbsTextEditor = popupView.findViewById(R.id.food_carbs_edit)
            _caloriesTextEditor = popupView.findViewById(R.id.food_calories_edit)
            _quantityTextEditor = popupView.findViewById(R.id.food_quantity_edit)

            _collapseBtn = popupView.findViewById(R.id.collapse_food_tools_btn)
            _addFoodBtn = popupView.findViewById(R.id.add_food_btn)
            _clearListBtn = popupView.findViewById(R.id.clear_food_list_btn)
            _closeFoodPopupButton = popupView.findViewById(R.id.close_food_popup_btn)

            _closeFoodPopupButton.setOnClickListener {
                popupWindow.dismiss()
            }

            _addFoodBtn.setOnClickListener {
                _toolsContainer.visibility = View.GONE
                _collapseBtn.visibility = View.VISIBLE

                val name = _nameTextEditor.text.toString()
                val description = _descriptionTextEditor.text.toString()
                val useByDate = _pickDateTimeClicker.text.toString()

                try {
                    val protein = _proteinTextEditor.text.toString().toDouble()
                    val fat = _fatTextEditor.text.toString().toDouble()
                    val carbs = _carbsTextEditor.text.toString().toDouble()
                    val calories = _caloriesTextEditor.text.toString().toDouble()
                    val quantity = _quantityTextEditor.text.toString().toByte()

                    if (name.isNotBlank() && useByDate.isNotBlank()) {
                        _viewModel.add(Food(
                            name,
                            useByDate,
                            description,
                            protein,
                            fat,
                            carbs,
                            calories,
                            quantity))

                        adapter.notifyItemInserted(_viewModel.count.value!!.minus(1))
                    }
                }
                catch (_: NumberFormatException) {

                }

                _nameTextEditor.text.clear()
                _descriptionTextEditor.text.clear()
                _pickDateTimeClicker.text = ""
                _proteinTextEditor.text.clear()
                _fatTextEditor.text.clear()
                _carbsTextEditor.text.clear()
                _caloriesTextEditor.text.clear()
                _quantityTextEditor.text.clear()
            }

            _collapseBtn.setOnClickListener {
                _toolsContainer.visibility = View.VISIBLE
                _collapseBtn.visibility = View.GONE
                _nameTextEditor.requestFocus()
            }

            _clearListBtn.setOnClickListener {
                val size = _viewModel.count.value!!
                _viewModel.clear()
                adapter.notifyItemRangeRemoved(0, size)
            }

            _viewModel.count.observe(viewLifecycleOwner) {
                _clearListBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
            }

            pickDateTime()

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {

    }

    override fun onItemLongClick(index: Int) {
        val item = _viewModel.getFood(index)

        val dialog = EditFoodDialogFragment.getInstance(this)
        dialog.setFood(item)
        dialog.show(requireActivity().supportFragmentManager, EditFoodDialogFragment.TAG)
    }

    override fun onItemClick(index: Int) { }

    override fun onPositive(food: Food) {
        _viewModel.update(food)
    }

    override fun onNegative(food: Food) {

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

    private fun getDateTimeCalendar() {
        val calendar = Calendar.getInstance(Locale.forLanguageTag("ru-RU"))

        _day = calendar.get(Calendar.DAY_OF_MONTH)
        _month = calendar.get(Calendar.MONTH)
        _year = calendar.get(Calendar.YEAR)
        _hour = calendar.get(Calendar.HOUR)
        _minute = calendar.get(Calendar.MINUTE)
    }

    private fun pickDateTime() {
        _pickDateTimeClicker.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, _year, _month, _day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        _savedDay = dayOfMonth
        _savedMonth = month
        _savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(requireContext(), this, _hour, _minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        _savedHour = hourOfDay
        _savedMinute = minute

        _pickDateTimeClicker.text = getString(R.string.food_format_date_edit, _savedDay, _savedMonth, _savedYear, _savedHour, _savedMinute)
    }

}