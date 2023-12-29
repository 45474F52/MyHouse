package com.aes.myhome.ui.food.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.ProductsAdapter
import com.aes.myhome.databinding.FragmentShoppingBinding
import com.aes.myhome.objects.Product
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingFragment : Fragment(),
    SendProductsDialog.ICallbackReceiver,
    BottomSheetCreateProduct.ICallbackReceiver,
    ItemTouchCallback.Receiver<Product> {

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
        _viewModel = ViewModelProvider(this)[ShoppingViewModel::class.java]

        val finishShoppingBtn = binding.finishShoppingBtn
        val backgroundImage = binding.backgroundImage

        _viewModel.products.observe(viewLifecycleOwner) { products ->
            _adapter = ProductsAdapter(products)

            val recycler: RecyclerView = binding.productsList
            recycler.adapter = _adapter
            recycler.layoutManager = LinearLayoutManager(context)

            ItemTouchHelper(ItemTouchCallback(
                0,
                ItemTouchHelper.RIGHT,
                products,
                recycler,
                this,
                getString(R.string.action_undo)
            )).attachToRecyclerView(recycler)

            finishShoppingBtn.setOnClickListener {
                showSendProductsDialog()
            }
        }

        val collapseBtn = binding.addProductRequestBtn

        collapseBtn.setOnClickListener {
            val bottomSheet = BottomSheetCreateProduct(this)
            bottomSheet.show(requireActivity().supportFragmentManager, BottomSheetCreateProduct.TAG)
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            finishShoppingBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
            backgroundImage.visibility = if (it == 0) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _snackBar?.dismiss()
    }

    override fun onNegative() {
        val tmp = _viewModel.count.value!!
        _viewModel.clearProducts()
        _adapter.notifyItemRangeRemoved(0, tmp)
        showChangeBudgetNotify()
    }

    override fun onPositive() {
        val tmp = _viewModel.count.value!!
        _viewModel.finishShopping()
        _adapter.notifyItemRangeRemoved(0, tmp)
        showChangeBudgetNotify()
    }

    private var _snackBar: Snackbar? = null
    private fun showChangeBudgetNotify() {
        _snackBar =
            Snackbar
            .make(requireView(), getString(R.string.budget_snackbar_action), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_toDo)) {
                Navigation
                    .findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                    .navigate(R.id.nav_food_shopping_navToBudget)
            }

        _snackBar!!.show()
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

    override fun onPostData(name: String, description: String) {
        _viewModel.addProduct(name, description)
        _adapter.notifyItemInserted(_viewModel.count.value!!.minus(1))
    }
}