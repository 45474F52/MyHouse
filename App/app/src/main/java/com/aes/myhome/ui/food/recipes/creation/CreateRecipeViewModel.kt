package com.aes.myhome.ui.food.recipes.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aes.myhome.objects.CheckableText
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeFoodCrossRef
import com.aes.myhome.storage.database.repositories.FoodRepository
import com.aes.myhome.storage.database.repositories.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _foods = MutableLiveData<List<Food>>()

    val foods: LiveData<List<Food>>
        get() = _foods

    init {
        viewModelScope.launch {
            val data = mutableListOf<Food>()

            withContext(Dispatchers.IO) {
                data.addAll(foodRepository.getAll())
            }

            withContext(Dispatchers.Main) {
                _foods.value = data
            }
        }
    }

    fun formatAsRecipesDateTime(date: Date): String = recipeRepository.dateFormat.format(date)

    fun saveRecipe(foods: List<CheckableText>, recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.insertAll(recipe)

            val lastItem = recipeRepository.getLast() ?:
                throw IllegalStateException("List of recipe records is empty")

            for (item in foods) {
                var food = _foods.value!!.find { f -> f.foodName == item.text }

                if (food == null) {
                    createFood(item.text)
                    food = _foods.value!!.last()
                }

                val crossRef = RecipeFoodCrossRef(food.foodId, lastItem.recipeId)
                recipeRepository.insertCrossRefWithFood(crossRef)
            }
        }
    }

    suspend fun createFood(foodName: String) {
        val food = Food(
            foodName,
            foodRepository.dateTimeFormat.format(Date()),
            "", .0, .0, .0, .0, 1)

        foodRepository.insertAll(food)

        val tmp = mutableListOf<Food>().apply {
            addAll(_foods.value!!)
            add(food)
        }

        _foods.value = tmp
    }
}