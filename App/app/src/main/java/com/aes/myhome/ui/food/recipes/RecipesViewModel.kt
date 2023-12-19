package com.aes.myhome.ui.food.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.repositories.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipesInternal = mutableListOf<Recipe>()

    private val _recipes = MutableLiveData<List<Recipe>>()
    private val _count = MutableLiveData<Int>()

    val count: LiveData<Int>
        get() = _count

    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    fun loadRecipes(listener: () -> Unit) {
        if (_recipesInternal.isEmpty()) {
            viewModelScope.launch {
                var data: List<Recipe>

                withContext(Dispatchers.IO) {
                    data = repository.getAll()
                }

                _recipesInternal.addAll(data)
                _recipes.value = _recipesInternal
                _count.value = data.size
                listener.invoke()
            }
        }
        else {
            _count.value = _recipesInternal.size
            listener.invoke()
        }
    }

    fun add(recipe: Recipe) {
        _recipesInternal.add(recipe)
        _recipes.value = _recipesInternal

        _count.value = _count.value!!.plus(1)

        addRecipes(recipe)
    }

//    @Throws(IllegalArgumentException::class)
//    fun update(recipe: Recipe) {
//        if (!recipes.value.isNullOrEmpty()) {
//            if (recipes.value!!.contains(recipe)) {
//                updateRecipes(recipe)
//            }
//            else {
//                throw IllegalArgumentException("Item not contained in list")
//            }
//        }
//    }

    private var _deletedItemIndex = -1
    fun requestToDelete(item: Recipe) {
        _deletedItemIndex = _recipesInternal.indexOf(item)

        _recipesInternal.removeAt(_deletedItemIndex)
        _recipes.value = _recipesInternal

        _count.value = count.value!!.minus(1)
    }

    fun confirmDeletion(item: Recipe) {
        _deletedItemIndex = -1

        deleteRecipes(item)
    }

    fun undoDeletion(item: Recipe) {
        if (_deletedItemIndex != -1) {
            _recipesInternal.add(_deletedItemIndex, item)
            _recipes.value = _recipesInternal

            _count.value = _count.value!!.plus(1)

            _deletedItemIndex = -1
        }
    }

    fun clear() {
        _recipesInternal.clear()
        _recipes.value = emptyList()
        _count.value = 0

        clearRecipes()
    }

    private fun addRecipes(vararg recipes: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(*recipes)
        }
    }

//    private fun updateRecipes(vararg recipes: Recipe) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateAll(*recipes)
//        }
//    }

    private fun deleteRecipes(item: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll(item)
        }
    }

    private fun clearRecipes() {
        if (!recipes.value.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll(*recipes.value!!.toTypedArray())
            }
        }
    }
}