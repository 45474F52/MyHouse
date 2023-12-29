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
    ItemTouchCallback.Receiver<Food>,
    EditFoodDialog.ICallbackReceiver,
    BottomSheetCreateFood.ICallbackReceiver {

    private var _binding: FragmentMenuFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: MenuFoodViewModel
    private lateinit var _adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuFoodBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[MenuFoodViewModel::class.java]

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

        val addFoodRequestBtn = binding.addFoodRequestBtn
        val clearListBtn: Button = binding.clearFoodListBtn
        val image = binding.backgroundImage

        addFoodRequestBtn.setOnClickListener {
            val bsh = BottomSheetCreateFood(this)
            bsh.show(requireActivity().supportFragmentManager, BottomSheetCreateFood.TAG)
        }

        clearListBtn.setOnClickListener {
            val size = _viewModel.count.value!!
            _viewModel.clear()
            _adapter.notifyItemRangeRemoved(0, size)
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            clearListBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
            image.visibility = if (it == 0) View.VISIBLE else View.GONE
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

    override fun onPostData(food: Food) {
        _viewModel.add(food)
        _adapter.notifyItemInserted(_viewModel.count.value!!.minus(1))
    }
}