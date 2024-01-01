package com.aes.myhome.ui.tasks.schedule

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.ScheduleEventAdapter
import com.aes.myhome.databinding.FragmentScheduleBinding
import com.aes.myhome.storage.database.entities.ScheduleEvent
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class ScheduleFragment : Fragment(),
    IItemClickListener,
    BottomSheetCreateEvent.ICallbackReceiver,
    EventEditDialog.ICallbackReceiver {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: ScheduleViewModel
    private lateinit var _adapter: ScheduleEventAdapter
    private lateinit var _editDialog: EventEditDialog

    private val _filteredEvents = mutableListOf<ScheduleEvent>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        _editDialog = EventEditDialog(this)

        val categoriesValues = resources.getStringArray(R.array.event_categories_values)
        val categoriesColors = resources.getIntArray(R.array.event_categories_colors)

        _viewModel.initialized.observe(viewLifecycleOwner) {
            _filteredEvents.addAll(_viewModel.events.value!!)

            _adapter = ScheduleEventAdapter(_filteredEvents, this) {
                val index = categoriesValues.indexOfFirst { x -> x == it }
                if (index != -1) categoriesColors[index] else 0xFFFFFF
            }

            val recycler = binding.scheduleRecycler
            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = _adapter
        }

        val bottomSheet = BottomSheetCreateEvent(this)

        val createEventBtn = binding.createEventBtn
        createEventBtn.setOnClickListener {
            bottomSheet.show(requireActivity().supportFragmentManager, BottomSheetCreateEvent.TAG)
        }

        val currentDateBtn = binding.scheduleCurDateBtn
        currentDateBtn.setOnClickListener {
            TODO("Move list to current date")
        }

        currentDateBtn.text = LocalDate.now().dayOfMonth.toString()

        val searchBtn = binding.scheduleSearchBtn
        searchBtn.setOnClickListener {
            TODO("Show search event view")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemLongClick(index: Int) { }

    private var _indexOfEditedItem = -1
    override fun onItemClick(index: Int) {
        _indexOfEditedItem = index
        _editDialog.show(requireActivity().supportFragmentManager, EventEditDialog.TAG)
    }

    override fun onPostData(scheduleEvent: ScheduleEvent) {
        _viewModel.addEvent(scheduleEvent)
        _adapter.notifyItemInserted(_viewModel.events.value!!.size - 1)
    }

    override fun onPositive(scheduleEvent: ScheduleEvent) {
        _viewModel.updateEvent(_indexOfEditedItem, scheduleEvent)
        _adapter.notifyItemChanged(_indexOfEditedItem)
        _indexOfEditedItem = -1
    }

    override fun onNegative() {
        _indexOfEditedItem = -1
    }
}