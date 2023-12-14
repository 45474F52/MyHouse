package com.aes.myhome.storage.database.repositories

import com.aes.myhome.storage.database.daos.RecipeDAO
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeWithFoods
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val recipeDAO: RecipeDAO) {
    /**
     * Формат даты и времени (dd.MM.yyyy). Locale = ru-RU
     */
    val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru-RU"))

    /**
     * Возвращает количество записей
     */
    suspend fun count() = recipeDAO.getAll().size

    /**
     * Возвращает все записи
     */
    suspend fun getAll() = recipeDAO.getAll()

    /**
     * Находит запись по Id
     */
    suspend fun findById(id: Int) = recipeDAO.findById(id)

    /**
     * Находит запись по name
     */
    suspend fun findByName(name: String) = recipeDAO.findByName(name)

    /**
     * Находит все записи, где name = param(name)
     */
    suspend fun getByName(name: String) = recipeDAO.getByName(name)

    /**
     * Вставляет записи
     */
    suspend fun insertAll(vararg recipes: Recipe) = recipeDAO.insertRecipes(*recipes)

    /**
     * Обновляет записи
     */
    suspend fun updateAll(vararg recipes: Recipe) = recipeDAO.updateRecipes(*recipes)

    /**
     * Удаляет записи
     */
    suspend fun deleteAll(vararg recipes: Recipe) = recipeDAO.deleteRecipes(*recipes)

    /**
     * Возвращает все записи RecipeWithFoods
     * @see RecipeWithFoods
     */
    suspend fun getRecipesWithFoods() = recipeDAO.getRecipesWithFoods()

    /**
     * Возвращает все продукты, которые используются в рецепте с переданным Id
     * @return запись RecipeWithFoods
     * @see RecipeWithFoods
     */
    suspend fun getFoodsInRecipe(recipeId: Int): RecipeWithFoods {
        val all = getRecipesWithFoods()
        return all.first { rwf -> rwf.recipe.recipeId == recipeId }
    }
}