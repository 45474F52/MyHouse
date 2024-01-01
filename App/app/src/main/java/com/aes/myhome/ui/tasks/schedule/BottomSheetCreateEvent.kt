package com.aes.myhome.ui.tasks.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aes.myhome.R
import com.aes.myhome.databinding.BottomSheetCreateScheduleEventBinding
import com.aes.myhome.storage.database.entities.ScheduleEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetCreateEvent(
    private val receiver: ICallbackReceiver
) : BottomSheetDialogFragment() {

    companion object {
        val TAG = BottomSheetCreateEvent::class.simpleName
    }

    interface ICallbackReceiver {
        fun onPostData(scheduleEvent: ScheduleEvent)
    }

    private var _binding: BottomSheetCreateScheduleEventBinding? = null
    private val binding: BottomSheetCreateScheduleEventBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            BottomSheetCreateScheduleEventBinding.inflate(inflater, container, false)

        val categoriesEntries = resources.getStringArray(R.array.event_categories_entries)
        val categoriesValues = resources.getStringArray(R.array.event_categories_values)

        val dataEdit = binding.eventDataEdit
        val dateSelect = binding.eventDateSelect
        val categorySelect = binding.eventCategorySelect
        val enableNotification = binding.eventEnableNotification
        val notificationDateTimeText = binding.eventNotificationDateTimeText

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

        val addEventBtn = binding.addEventBtn
        addEventBtn.setOnClickListener {
            val event = manager.createEvent() ?: return@setOnClickListener

            receiver.onPostData(event)

            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventDataEdit.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}