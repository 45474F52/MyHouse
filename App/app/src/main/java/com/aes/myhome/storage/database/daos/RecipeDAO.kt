package com.aes.myhome.storage.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeWithFoods

@Dao
interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE recipeId = :id")
    suspend fun findById(id: Int) : Recipe?

    @Query("SELECT * FROM Recipe WHERE RecipeName = :name LIMIT 1")
    suspend fun findByName(name: String) : Recipe

    @Query("SELECT * FROM Recipe WHERE RecipeName LIKE :name")
    suspend fun getByName(name: String) : List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(vararg recipes: Recipe)

    @Update
    suspend fun updateRecipes(vararg recipes: Recipe)

    @Delete
    suspend fun deleteRecipes(vararg recipes: Recipe)

    @Transaction
    @Query("SELECT * FROM Recipe")
    suspend fun getRecipesWithFoods() : List<RecipeWithFoods>
}