package com.aes.myhome.ui.food.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.repositories.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    val recipeList: ArrayList<Recipe> = arrayListOf()

    private val _count = MutableLiveData<Int>()

    private fun updateCount() {
        _count.value = recipeList.size
    }

    val count: LiveData<Int>
        get() = _count

    fun updateRecipes() = runBlocking {
        launch {
            recipeList.clear()
            recipeList.addAll(repository.getAll())
            updateCount()
        }
    }

    fun getRecipe(index: Int): Recipe {
        return recipeList[index]
    }

    fun add(recipe: Recipe) {
        addRecipes(recipe)
        updateCount()
    }

    @Throws(IllegalArgumentException::class)
    fun update(recipe: Recipe) {
        if (recipeList.contains(recipe)) {
            updateRecipes(recipe)
        }
        else
            throw IllegalArgumentException("Item not contained in list")
    }

    private var _currentItemToDelete = -1
    fun requestToDelete(index: Int) {
        _currentItemToDelete = index
        _count.value = count.value!!.minus(1)
    }

    fun confirmDeletion(item: Recipe) {
        delete(item)
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
        clearRecipes()
        recipeList.clear()
        updateCount()
    }

    private fun delete(item: Recipe) {
        deleteRecipes(item)
    }

    private fun addRecipes(vararg recipes: Recipe) = runBlocking {
        launch {
            repository.insertAll(*recipes)
        }
    }

    private fun updateRecipes(vararg recipes: Recipe) = runBlocking {
        launch {
            repository.updateAll(*recipes)
        }
    }

    private fun deleteRecipes(item: Recipe) = runBlocking {
        launch {
            repository.deleteAll(item)
        }
    }

    private fun clearRecipes() = runBlocking {
        launch {
            repository.deleteAll(*recipeList.toTypedArray())
        }
    }
}