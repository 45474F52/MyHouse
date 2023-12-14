package com.aes.myhome.ui.food.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.databinding.FragmentShoppingBinding
import com.aes.myhome.objects.Product
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingFragment : Fragment(), OnClickListener, SendProductsDialogFragment.ICallbackReceiver {

    @Inject lateinit var repository: FoodRepository

    private lateinit var _viewModel: ShoppingViewModel

    private lateinit var _toolsContainer: LinearLayout
    private lateinit var _nameTextEditor: EditText
    private lateinit var _descriptionTextEditor: EditText
    private lateinit var _collapseBtn: Button
    private lateinit var _addProductBtn: Button
    private lateinit var _finishShoppingBtn: Button
    private lateinit var _clearListBtn: Button
    private lateinit var _bottomButtons: LinearLayout

    private var _binding: FragmentShoppingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShoppingBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this,
            ShoppingVMFactory(
                serializer = JsonDataSerializer(requireContext(), JsonDataSerializer.StorageType.INTERNAL),
                repository = repository
            ))[ShoppingViewModel::class.java]

        val recycler = binding.root.findViewById<RecyclerView>(R.id.products_list)
        recycler.adapter = _viewModel.adapter
        recycler.layoutManager = LinearLayoutManager(context)

        val update = fun(_: Int, _: Product) { _viewModel.updateValues() }

        ItemTouchHelper(ItemTouchCallback(
            0, ItemTouchHelper.RIGHT, _viewModel.products, recycler, _viewModel.adapter, update, update, {}))
            .attachToRecyclerView(recycler)

        _viewModel.updateProducts()

        _toolsContainer = binding.root.findViewById(R.id.tools_container)
        _bottomButtons = binding.root.findViewById(R.id.bottom_shopping_buttons)

        _nameTextEditor = binding.root.findViewById(R.id.product_name_edit)
        _descriptionTextEditor = binding.root.findViewById(R.id.product_description_edit)

        _collapseBtn = binding.root.findViewById(R.id.collapse_tools_btn)
        _addProductBtn = binding.root.findViewById(R.id.add_product_btn)
        _finishShoppingBtn = binding.root.findViewById(R.id.finish_shopping_btn)
        _clearListBtn = binding.root.findViewById(R.id.clear_list_btn)

        setOnClickListenerTo(_collapseBtn, _addProductBtn, _finishShoppingBtn, _clearListBtn)

        _viewModel.count.observe(viewLifecycleOwner) {
            _bottomButtons.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    private fun setOnClickListenerTo(vararg buttons: Button) {
        for (button in buttons) {
            button.setOnClickListener(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_product_btn -> {
                _descriptionTextEditor.clearFocus()
                _nameTextEditor.clearFocus()
                hideVirtualKeyboard()
                _toolsContainer.visibility = View.GONE
                _collapseBtn.visibility = View.VISIBLE
                addProduct()
            }
            R.id.collapse_tools_btn -> {
                _toolsContainer.visibility = View.VISIBLE
                _collapseBtn.visibility = View.GONE
                _nameTextEditor.requestFocus()
                showVirtualKeyboard()
            }
            R.id.finish_shopping_btn -> {
                showSendProductsDialog()
            }
            R.id.clear_list_btn -> {
                _viewModel.clearProducts()
            }
            else -> throw NotImplementedError("Has not on click implementation! Id = $v.id")
        }
    }

    private fun showSendProductsDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        val dialog = SendProductsDialogFragment.getInstance(this)
        dialog.show(fragmentManager, SendProductsDialogFragment.TAG)
    }

    override fun onNegative(dialog: DialogFragment) {
        _viewModel.clearProducts()
    }

    override fun onPositive(dialog: DialogFragment) {
        _viewModel.finishShopping()
    }

    private fun addProduct() {
        val name = _nameTextEditor.text.toString()
        val description = _descriptionTextEditor.text.toString()

        if (name.isNotBlank()) {
            _viewModel.addProduct(name, description)
        }

        _nameTextEditor.text.clear()
        _descriptionTextEditor.text.clear()
    }

    private fun hideVirtualKeyboard() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.let {
            it.hideSoftInputFromWindow(_nameTextEditor.windowToken, 0)
            it.hideSoftInputFromWindow(_descriptionTextEditor.windowToken, 0)
        }
    }

    private fun showVirtualKeyboard() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(_nameTextEditor, 0)
    }
}