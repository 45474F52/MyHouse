package com.aes.myhome.ui.food.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.objects.Product
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.json.JsonDataSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ShoppingViewModel(
    private val serializer: JsonDataSerializer,
    private val repository: FoodRepository): ViewModel()
{

    private val _productsInternal = mutableListOf<Product>()
    private val _products = MutableLiveData<List<Product>>()
    private val _count = MutableLiveData<Int>()

    val products: LiveData<List<Product>>
        get() = _products

    val count: LiveData<Int>
        get() = _count

    fun loadProducts(listener: () -> Unit) {
        if (_productsInternal.isEmpty()) {
            viewModelScope.launch {
                var data: List<Product>?

                withContext(Dispatchers.IO) {
                    data = serializer.deserialize("", "products.json")
                }

                if (data == null) {
                    data = emptyList()
                }

                _productsInternal.addAll(data!!)
                _products.value = _productsInternal

                _count.value = _productsInternal.size
                listener.invoke()
            }
        }
        else {
            listener.invoke()
        }
    }

    fun addProduct(name: String, description: String) {
        _productsInternal.add(Product(name, description))
        _products.value = _productsInternal
        _count.value = _count.value!!.plus(1)

        saveProducts()
    }

    private var _indexToDelete = -1
    fun requestToDelete(index: Int) {
        _indexToDelete = index

        _productsInternal.removeAt(index)
        _products.value = _productsInternal

        _count.value = count.value!!.minus(1)
    }

    fun confirmDeletion() {
        _indexToDelete = -1

        saveProducts()
    }

    fun undoDeletion(index: Int, item: Product) {
        if (_indexToDelete != -1) {
            _productsInternal.add(index, item)
            _products.value = _productsInternal

            _count.value = _count.value!!.plus(1)

            _indexToDelete = -1
        }
    }

    fun clearProducts() {
        _productsInternal.clear()
        _products.value = emptyList()
        _count.value = 0

        saveProducts()
    }

    fun finishShopping() {
        saveProductsAsFoods()
        clearProducts()
    }

    private fun saveProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            serializer.serialize(products.value, "", "products.json")
        }
    }

    private fun saveProductsAsFoods() {
        viewModelScope.launch {
            val foods = arrayListOf<Food>()

            for (product in products.value!!) {
                val food = Food(
                    product.name,
                    repository.dateTimeFormat.format(Date()),
                    product.description,
                    .0,.0,.0,.0,1)

                foods.add(food)
            }

            withContext(Dispatchers.IO) {
                repository.insertAll(*foods.toTypedArray())
            }
        }
    }
}