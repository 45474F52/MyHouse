package com.aes.myhome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.objects.CardsMap
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val serializer: JsonDataSerializer) : ViewModel() {

    private companion object {
        private const val FILE_NAME = "fast_views.json"
    }

    private val _cardsMapInternal = mutableListOf<CardsMap>()
    private val _cardsMap = MutableLiveData<List<CardsMap>>()

    init {
        viewModelScope.launch {
            var data: MutableList<CardsMap>?

            withContext(Dispatchers.IO) {
                data = serializer.deserialize("", FILE_NAME)
            }

            withContext(Dispatchers.Main) {
                if (data != null) {
                    _cardsMapInternal.addAll(data!!)
                }

                _cardsMap.value = _cardsMapInternal
            }
        }
    }

    val cardsMap: LiveData<List<CardsMap>>
        get() = _cardsMap

    fun add(item: CardsMap) {
        _cardsMapInternal.add(item)
        _cardsMap.value = _cardsMapInternal
    }

    fun clear() {
        _cardsMapInternal.clear()
        _cardsMap.value = emptyList()
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            serializer.serialize(_cardsMapInternal, "", FILE_NAME)
        }
    }
}