package com.aes.myhome.ui.food.shopping

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aes.myhome.R
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
                .setTitle(getString(R.string.dialog_sendProducts_title))
                .setMessage(getString(R.string.dialog_sendProducts_message))
                .setPositiveButton(getString(R.string.action_yes)) { _, _ ->
                    _receiver.onPositive()
                }
                .setNegativeButton(getString(R.string.action_no)) { _, _ ->
                    _receiver.onNegative()
                }
                .setNeutralButton(getString(R.string.action_cancel)) { _, _ ->  }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}