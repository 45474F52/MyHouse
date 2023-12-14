package com.aes.myhome.ui.food.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aes.myhome.adapters.ProductsAdapter
import com.aes.myhome.objects.Product
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.json.JsonDataSerializer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class ShoppingViewModel(
    private val serializer: JsonDataSerializer,
    private val repository: FoodRepository) : ViewModel() {

    val products: ArrayList<Product> = arrayListOf()
    val adapter: ProductsAdapter = ProductsAdapter(products)

    private fun saveProducts() {
        serializer.serialize(products, "", "products.json")
    }

    private suspend fun saveProductsAsFoods() {
        val foods = arrayListOf<Food>()

        for (product in products) {
            val food = Food(
                product.name,
                repository.dateTimeFormat.format(Date()),
                product.description,
                .0,.0,.0,.0,1)

            foods.add(food)
        }

        repository.insertAll(*foods.toTypedArray())
    }

    private fun updateCount() {
        _count.value = adapter.itemCount
    }

    private val _count = MutableLiveData<Int>()

    /**
     * Количество продуктов
     */
    val count: LiveData<Int>
        get() = _count

    /**
     * Обновляет список продуктов из сохранений,
     * обновляет состояние количества продуктов
     */
    fun updateProducts() {
        val data = serializer.deserialize<ArrayList<Product>>("", "products.json")
        if (products.size == 0) {
            if (data != null) {
                products.addAll(data)
                adapter.notifyItemRangeInserted(0, data.size)
            }
        }

        updateCount()
    }

    /**
     * Создаёт продукт и добавляет его в конец списка продуктов,
     * обновляет состояние количества продуктов,
     * сохраняет текущий список продуктов
     */
    fun addProduct(name: String, description: String) {
        products.add(Product(name, description))
        adapter.notifyItemInserted(products.size - 1)
        updateCount()
        saveProducts()
    }

    /**
     * Обновляет состояние количества продуктов,
     * сохраняет текущий список продуктов
     */
    fun updateValues() {
        updateCount()
        saveProducts()
    }

    /**
     * Очищает список покупок,
     * обновляет состояние количества продуктов,
     * сохраняет текущий список продуктов
     */
    fun clearProducts() {
        products.clear()
        adapter.notifyItemRangeRemoved(0, _count.value!!)
        updateCount()
        saveProducts()
    }

    /**
     * Сохраняет купленные продукты,
     * вызывает метод clearProducts
     * @see clearProducts
     */
    fun finishShopping() = runBlocking {
        launch {
            saveProductsAsFoods()
            clearProducts()
        }
    }
}