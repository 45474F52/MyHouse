package com.aes.myhome.ui.food.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.json.JsonDataSerializer
import javax.inject.Inject

class ShoppingVMFactory @Inject constructor(
    private val serializer: JsonDataSerializer,
    private val repository: FoodRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShoppingViewModel(serializer, repository) as T
    }
}