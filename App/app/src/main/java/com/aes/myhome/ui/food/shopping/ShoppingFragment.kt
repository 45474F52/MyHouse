package com.aes.myhome.ui.food.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.ProductsAdapter
import com.aes.myhome.databinding.FragmentShoppingBinding
import com.aes.myhome.objects.Product
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingFragment : Fragment(),
    SendProductsDialog.ICallbackReceiver,
    ItemTouchCallback.Receiver<Product> {

    @Inject lateinit var repository: FoodRepository

    private lateinit var _viewModel: ShoppingViewModel
    private lateinit var _adapter: ProductsAdapter

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

        val finishShoppingBtn: Button = binding.root.findViewById(R.id.finish_shopping_btn)
        val clearListBtn: Button = binding.root.findViewById(R.id.clear_list_btn)

        _viewModel.products.observe(viewLifecycleOwner) { products ->
            _adapter = ProductsAdapter(products)

            val recycler: RecyclerView = binding.root.findViewById(R.id.products_list)
            recycler.adapter = _adapter
            recycler.layoutManager = LinearLayoutManager(context)

            ItemTouchHelper(ItemTouchCallback(
                0,
                ItemTouchHelper.RIGHT,
                products,
                recycler,
                this))
                .attachToRecyclerView(recycler)

            finishShoppingBtn.setOnClickListener {
                showSendProductsDialog()
            }

            clearListBtn.setOnClickListener {
                val tmp = products.size
                _viewModel.clearProducts()
                _adapter.notifyItemRangeRemoved(0, tmp)
            }
        }

        val toolsContainer: LinearLayout = binding.root.findViewById(R.id.tools_container)
        val bottomButtons: LinearLayout = binding.root.findViewById(R.id.bottom_shopping_buttons)

        val nameTextEditor: EditText = binding.root.findViewById(R.id.product_name_edit)
        val descriptionTextEditor: EditText = binding.root.findViewById(R.id.product_description_edit)

        val collapseBtn: Button = binding.root.findViewById(R.id.collapse_tools_btn)
        val addProductBtn: Button = binding.root.findViewById(R.id.add_product_btn)

        collapseBtn.setOnClickListener {
            toolsContainer.visibility = View.VISIBLE
            collapseBtn.visibility = View.GONE
            nameTextEditor.requestFocus()
        }

        addProductBtn.setOnClickListener {
            toolsContainer.visibility = View.GONE
            collapseBtn.visibility = View.VISIBLE

            val name = nameTextEditor.text.toString()
            val description = descriptionTextEditor.text.toString()

            if (name.isNotBlank()) {
                _viewModel.addProduct(name, description)
                _adapter.notifyItemInserted(_viewModel.count.value!!.minus(1))
            }

            nameTextEditor.text.clear()
            descriptionTextEditor.text.clear()
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            bottomButtons.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNegative() {
        val tmp = _viewModel.count.value!!
        _viewModel.clearProducts()
        _adapter.notifyItemRangeRemoved(0, tmp)
    }

    override fun onPositive() {
        val tmp = _viewModel.count.value!!
        _viewModel.finishShopping()
        _adapter.notifyItemRangeRemoved(0, tmp)
    }

    private fun showSendProductsDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        val dialog = SendProductsDialog.getInstance(this)
        dialog.show(fragmentManager, SendProductsDialog.TAG)
    }

    override fun onDelete(index: Int, item: Product) {
        _viewModel.requestToDelete(index)
        _adapter.notifyItemRemoved(index)
    }

    override fun onUndo(index: Int, item: Product) {
        _viewModel.undoDeletion(index, item)
        _adapter.notifyItemInserted(index)
    }

    override fun onDismissed(item: Product) {
        _viewModel.confirmDeletion()
    }
}