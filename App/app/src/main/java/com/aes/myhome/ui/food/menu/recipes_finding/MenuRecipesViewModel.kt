package com.aes.myhome.ui.food.menu.recipes_finding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.storage.database.entities.FoodWithRecipes
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.database.repositories.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MenuRecipesViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel()
{
    private val _foodWithRecipes = mutableListOf<FoodWithRecipes>()

    private val _foundRecipes = MutableLiveData<List<Recipe>>()
    private val _recommendedRecipes = MutableLiveData<List<Recipe>>()

    val foundRecipes: LiveData<List<Recipe>>
        get() = _foundRecipes
    val recommendedRecipes: LiveData<List<Recipe>>
        get() = _recommendedRecipes

    fun loadData(listener: () -> Unit) {
        viewModelScope.launch {
            loadFoodWithRecipes()
            //findRecipes()

            if (_foodWithRecipes.size <= 500) {
                delay(3000L)
            }

            listener.invoke()
        }
    }

    private suspend fun loadFoodWithRecipes() = viewModelScope.launch {
        var data: List<FoodWithRecipes>

        withContext(Dispatchers.IO) {
            data = foodRepository.getFoodsWithRecipes()
        }

        _foodWithRecipes.addAll(data)
    }

    private suspend fun findRecipes() = viewModelScope.launch {
        val listOfFoods = _foodWithRecipes.map { x -> x.food }

        val listOfRecipes = mutableListOf<Recipe>()
        val listOfRecommendedRecipes = mutableListOf<Recipe>()

        for (recipes in _foodWithRecipes.map { x -> x.recipes }) {
            for (recipe in recipes) {
                withContext(Dispatchers.IO) {
                    val recipeWithFoods = recipeRepository.getFoodsInRecipe(recipe.recipeId)

                    if (recipeWithFoods.foods.containsAll(listOfFoods)) {
                        listOfRecipes.add(recipe)
                    }
                    else {
                        listOfRecommendedRecipes.add(recipe)
                    }
                }
            }
        }

        if (listOfRecipes.any()) {
            _foundRecipes.value = listOfRecipes
        }

        if (listOfRecommendedRecipes.any()) {
            _recommendedRecipes.value = listOfRecommendedRecipes
        }
    }
}