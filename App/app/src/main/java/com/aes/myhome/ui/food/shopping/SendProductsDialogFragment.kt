package com.aes.myhome.ui.food.shopping

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.io.Serializable

class SendProductsDialogFragment : DialogFragment() {

    private lateinit var _receiver: ICallbackReceiver

    interface ICallbackReceiver : Serializable {
        fun onPositive(dialog: DialogFragment)
        fun onNegative(dialog: DialogFragment)
    }

    companion object {
        private const val RECEIVER_KEY = "receiver_key"

        val TAG = SendProductsDialogFragment::class.simpleName

        fun getInstance(receiver: ICallbackReceiver): SendProductsDialogFragment {
            return Bundle().apply {
                putSerializable(RECEIVER_KEY, receiver)
            }.let {
                SendProductsDialogFragment().apply { arguments = it }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _receiver = arguments?.getSerializable(RECEIVER_KEY) as ICallbackReceiver
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder
                .setTitle("Добавить купленные продукты в список?")
                .setMessage("Это позволит отслеживать их срок годности, а так же создавать с ними рецепты")
                .setPositiveButton("Да") { _, _ ->
                    _receiver.onPositive(this)
                }
                .setNegativeButton("Нет") { _, _ ->
                    _receiver.onNegative(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}