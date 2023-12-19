package com.aes.myhome.ui.food.shopping

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.io.Serializable

class SendProductsDialog : DialogFragment() {

    private lateinit var _receiver: ICallbackReceiver

    interface ICallbackReceiver : Serializable {
        fun onPositive()
        fun onNegative()
    }

    companion object {
        private const val RECEIVER_KEY = "receiver_key"

        val TAG = SendProductsDialog::class.simpleName

        fun getInstance(receiver: ICallbackReceiver): SendProductsDialog {
            return Bundle().apply {
                putSerializable(RECEIVER_KEY, receiver)
            }.let {
                SendProductsDialog().apply { arguments = it }
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
                    _receiver.onPositive()
                }
                .setNegativeButton("Нет") { _, _ ->
                    _receiver.onNegative()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}