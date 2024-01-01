package com.aes.myhome.ui.tasks.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.storage.database.entities.ScheduleEvent
import com.aes.myhome.storage.database.repositories.ScheduleEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleEventRepository
) : ViewModel() {

    private var _initialized = MutableLiveData<Boolean>()
    private val _eventsInternal = mutableListOf<ScheduleEvent>()
    private val _events = MutableLiveData<List<ScheduleEvent>>()

    val events: LiveData<List<ScheduleEvent>>
        get() = _events

    val initialized: LiveData<Boolean>
        get() = _initialized

    init {
        viewModelScope.launch {
            val data = mutableListOf<ScheduleEvent>()

            withContext(Dispatchers.IO) {
                data.addAll(repository.getAll())
            }

            withContext(Dispatchers.Main) {
                _eventsInternal.addAll(data)
                _events.value = _eventsInternal
                _initialized.value = true
            }
        }
    }

    fun addEvent(item: ScheduleEvent) {
        viewModelScope.launch {

            _eventsInternal.add(item)
            _events.value = _eventsInternal

            withContext(Dispatchers.IO) {
                repository.insertAll(item)
            }
        }
    }

    fun updateEvent(index: Int, item: ScheduleEvent) {
        viewModelScope.launch {

            _eventsInternal[index] = item
            _events.value = _eventsInternal

            withContext(Dispatchers.IO) {
                repository.updateAll(item)
            }
        }
    }

}