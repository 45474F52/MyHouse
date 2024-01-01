package com.aes.myhome.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.aes.myhome.R
import java.io.Serializable

class ChooseFastViewDialog(private val receiver: ICallbackReceiver)
    : DialogFragment(), AdapterView.OnItemSelectedListener {

    private var _itemTitle = ""
    private var _itemIndex = -1

    interface ICallbackReceiver : Serializable {
        fun onPositive(title: String, index: Int)
        fun onNegative()
    }

    companion object {
        val TAG = ChooseFastViewDialog::class.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val dialogView: View = inflater.inflate(R.layout.dialog_choose_fast_view, null)

            val spinner: Spinner = dialogView.findViewById(R.id.dialog_choose_spinner)
            spinner.onItemSelectedListener = this

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.fast_views_entries,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            builder
                .setView(dialogView)
                .setTitle(getString(R.string.dialog_fastView_title))
                .setPositiveButton(getString(R.string.action_save)) { _, _ ->
                    receiver.onPositive(_itemTitle, _itemIndex)
                }
                .setNegativeButton(getString(R.string.action_cancel)) { _, _ ->
                    receiver.onNegative()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position)
        _itemTitle = item.toString()
        _itemIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _itemTitle = ""
        _itemIndex = -1
    }
}