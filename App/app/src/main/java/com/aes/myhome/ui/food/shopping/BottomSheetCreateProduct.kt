package com.aes.myhome.ui.food.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aes.myhome.R
import com.aes.myhome.databinding.BottomSheetCreateProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetCreateProduct(
    private val receiver: ICallbackReceiver
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCreateProductBinding? = null
    private val binding: BottomSheetCreateProductBinding
        get() = _binding!!

    interface ICallbackReceiver {
        fun onPostData(name: String, description: String)
    }

    companion object {
        val TAG = BottomSheetCreateProduct::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateProductBinding.inflate(inflater, container, false)

        val nameEdit = binding.productNameEdit
        val descriptionEdit = binding.productDescriptionEdit
        val addProductBtn = binding.addProductBtn

        addProductBtn.setOnClickListener {
            val name = nameEdit.text.toString()
            val description = descriptionEdit.text.toString()

            if (name.isNotBlank()) {
                receiver.onPostData(name, description)
            }

            nameEdit.text.clear()
            descriptionEdit.text.clear()

            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productNameEdit.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}