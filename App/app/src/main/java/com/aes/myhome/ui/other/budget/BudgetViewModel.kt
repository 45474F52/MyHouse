package com.aes.myhome.ui.other.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.DIHandler
import com.aes.myhome.objects.finances.UserFinances
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val serializer: JsonDataSerializer
) : ViewModel() {

    private val _userFinances = MutableLiveData<UserFinances>()

    val userFinances: LiveData<UserFinances>
        get() = _userFinances

    init {
        viewModelScope.launch {
            var data: UserFinances?

            withContext(Dispatchers.IO) {
                data = serializer.deserialize("", "finances.json")
            }

            withContext(Dispatchers.Main) {
                if (data != null) {
                    _userFinances.value = data!!
                }
                else {
                    _userFinances.value = UserFinances()
                }
            }
        }
    }

    fun saveData() {
        viewModelScope.launch(Dispatchers.IO) {
            serializer.serialize(_userFinances.value, "", "finances.json")
        }
    }
}