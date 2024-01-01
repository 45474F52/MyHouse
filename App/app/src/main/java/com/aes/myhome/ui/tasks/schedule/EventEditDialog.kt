package com.aes.myhome.ui.tasks.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.ScheduleEvent
import com.google.android.material.textfield.TextInputEditText

class EventEditDialog(
    private val receiver: ICallbackReceiver
) : DialogFragment() {

    companion object {
        val TAG = EventEditDialog::class.simpleName
    }

    interface ICallbackReceiver {
        fun onPositive(scheduleEvent: ScheduleEvent)
        fun onNegative()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val dialogView: View = inflater.inflate(R.layout.dialog_edit_schedule_event, null)

            val categoriesEntries = resources.getStringArray(R.array.event_categories_entries)
            val categoriesValues = resources.getStringArray(R.array.event_categories_values)

            val dataEdit: TextInputEditText =
                dialogView.findViewById(R.id.event_data_edit)
            val dateSelect: AutoCompleteTextView =
                dialogView.findViewById(R.id.event_date_select)
            val categorySelect: AutoCompleteTextView =
                dialogView.findViewById(R.id.event_category_select)
            val enableNotification: CheckBox =
                dialogView.findViewById(R.id.event_enable_notification)
            val notificationDateTimeText: TextView =
                dialogView.findViewById(R.id.event_notificationDateTime_text)

            val manager = EventDataManager(
                categoriesEntries,
                categorySelect,
                ::requireContext,
                categoriesValues,
                dataEdit,
                dateSelect,
                enableNotification,
                notificationDateTimeText
            )

            builder
                .setView(dialogView)
                .setTitle(R.string.title_recommended_recipes) // TODO
                .setPositiveButton(R.string.action_apply) { _, _ ->
                    val event = manager.createEvent() ?: return@setPositiveButton
                    receiver.onPositive(event)
                }
                .setNegativeButton(R.string.action_cancel) { _, _ ->
                    receiver.onNegative()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}