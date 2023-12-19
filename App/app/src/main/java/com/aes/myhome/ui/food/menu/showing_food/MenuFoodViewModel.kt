package com.aes.myhome.ui.food.menu.showing_food

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.FoodWithRecipes
import com.aes.myhome.storage.database.repositories.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MenuFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _foodInternal = mutableListOf<Food>()

    private val _food = MutableLiveData<List<Food>>()
    private val _count = MutableLiveData<Int>()
    private val _recipes = MutableLiveData<List<FoodWithRecipes>>()

    val count: LiveData<Int>
        get() = _count

    val food: LiveData<List<Food>>
        get() = _food

    val recipes: LiveData<List<FoodWithRecipes>>
        get() = _recipes

    fun loadFood(listener: () -> Unit) {
        if (_foodInternal.isEmpty()) {
            viewModelScope.launch {
                var data: List<Food>

                withContext(Dispatchers.IO) {
                    data = foodRepository.getAll()
                }

                _foodInternal.addAll(data)
                _food.value = _foodInternal
                _count.value = data.size
                listener.invoke()
            }
        }
        else {
            _count.value = _foodInternal.size
            listener.invoke()
        }
    }

    fun add(food: Food) {
        _foodInternal.add(food)
        _food.value = _foodInternal

        _count.value = _count.value!!.plus(1)

        addFood(food)
    }

    @Throws(IllegalArgumentException::class)
    fun update(item: Food) {
        if (food.value!!.contains(item)) {
            updateFood(item)
        }
        else
            throw IllegalArgumentException("Item not found in list")
    }

    private var _indexToDelete = -1
    fun requestToDelete(index: Int) {
        _indexToDelete = index

        _foodInternal.removeAt(index)
        _food.value = _foodInternal

        _count.value = count.value!!.minus(1)
    }

    fun confirmDeletion(item: Food) {
        _indexToDelete = -1

        deleteFood(item)
    }

    fun undoDeletion(index: Int, item: Food) {
        if (_indexToDelete != -1) {
            _foodInternal.add(index, item)
            _food.value = _foodInternal

            _count.value = _count.value!!.plus(1)

            _indexToDelete = -1
        }
    }

    fun clear() {
        _foodInternal.clear()
        _food.value = emptyList()
        _count.value = 0

        clearFood()
    }

    private fun addFood(vararg foods: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.insertAll(*foods)
        }
    }

    private fun updateFood(vararg foods: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.updateAll(*foods)
        }
    }

    private fun deleteFood(item: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.deleteAll(item)
        }
    }

    private fun clearFood() {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.deleteAll(*food.value!!.toTypedArray())
        }
    }

    fun updateRecipesWithFood() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipes.postValue(foodRepository.getFoodsWithRecipes())
        }
    }
}