package com.aes.myhome.storage.database.repositories

import com.aes.myhome.storage.database.daos.FoodDAO
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.FoodWithRecipes
import com.aes.myhome.storage.database.entities.RecipeFoodCrossRef
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodDAO: FoodDAO
) {

    val dateTimeFormat: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    /**
     * Возвращает количество записей
     */
    suspend fun count() = foodDAO.getAll().size

    /**
     * Возвращает все записи
     */
    suspend fun getAll() = foodDAO.getAll()

    /**
     * Находит запись по Id
     */
    suspend fun findById(id: Int) = foodDAO.findById(id)

    /**
     * Находит запись по name
     */
    suspend fun findByName(name: String) = foodDAO.findByName(name)

    /**
     * Находит все записи, где name = param(name)
     */
    suspend fun getByName(name: String) = foodDAO.getByName(name)

    /**
     * Вставляет записи
     */
    suspend fun insertAll(vararg foods: Food) = foodDAO.insertFoods(*foods)

    /**
     * Обновляет записи
     */
    suspend fun updateAll(vararg foods: Food) = foodDAO.updateFoods(*foods)

    /**
     * Удаляет записи
     */
    suspend fun deleteAll(vararg foods: Food) = foodDAO.deleteFoods(*foods)

    /**
     * Возвращает все записи FoodWithRecipes
     * @see FoodWithRecipes
     */
    suspend fun getFoodsWithRecipes() = foodDAO.getFoodsWithRecipes()

    /**
     * Возвращает рецепты, в которых используется продукт с переданным Id
     * @return запись FoodWithRecipes
     * @see FoodWithRecipes
     */
    suspend fun getRecipesWithFood(foodId: Int): FoodWithRecipes {
        val all = getFoodsWithRecipes()
        return all.first { fwr -> fwr.food.foodId == foodId }
    }

    suspend fun insertCrossRefWithRecipe(crossRef: RecipeFoodCrossRef) =
        foodDAO.insertCrossRefWithRecipe(crossRef)
}