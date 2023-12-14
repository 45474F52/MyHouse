package com.aes.myhome.ui.food.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.repositories.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: FoodRepository) : ViewModel() {

    val foodList: ArrayList<Food> = arrayListOf()

    private fun updateCount() {
        _count.value = foodList.size
    }

    private val _count = MutableLiveData<Int>()

    /**
     * Количество продуктов
     */
    val count: LiveData<Int>
        get() = _count

    fun updateFood() = runBlocking {
        launch {
            foodList.clear()
            foodList.addAll(repository.getAll())
            updateCount()
        }
    }

    fun getFood(id: Int): Food {
        return foodList[id]
    }

    fun add(food: Food) {
        addFood(food)
        updateCount()
    }

    @Throws(IllegalArgumentException::class)
    fun update(food: Food) {
        if (foodList.contains(food)) {
            updateFood(food)
        }
        else
            throw IllegalArgumentException("Item not contained in list")
    }

    private var _currentItemToDelete = -1
    fun requestToDelete(id: Int) {
        _currentItemToDelete = id
        _count.value = count.value!!.minus(1)
    }

    fun confirmDeletion() {
        delete(_currentItemToDelete)
        updateCount()
        _currentItemToDelete = -1
    }

    fun undoDeletion() {
        if (_currentItemToDelete != -1) {
            updateCount()
            _currentItemToDelete = -1
        }
    }

    fun clear() {
        clearFood()
        foodList.clear()
        updateCount()
    }

    private fun delete(id: Int) {
        val item = foodList[id]
        deleteFood(item, id)
        foodList.removeAt(id)
    }

    private fun addFood(vararg foods: Food) = runBlocking {
        launch {
            repository.insertAll(*foods)
        }
    }

    private fun updateFood(vararg foods: Food) = runBlocking {
        launch {
            repository.updateAll(*foods)
        }
    }

    private fun deleteFood(item: Food, index: Int) = runBlocking {
        launch {
            repository.deleteAll(item)
        }
    }

    private fun clearFood() = runBlocking {
        launch {
            repository.deleteAll(*foodList.toTypedArray())
        }
    }
}