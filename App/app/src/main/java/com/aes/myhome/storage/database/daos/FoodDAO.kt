package com.aes.myhome.storage.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.FoodWithRecipes

@Dao
interface FoodDAO {
    @Query("SELECT * FROM Food")
    suspend fun getAll(): List<Food>

    @Query("SELECT * FROM Food WHERE foodId = :id")
    suspend fun findById(id: Int) : Food?

    @Query("SELECT * FROM Food WHERE FoodName = :name LIMIT 1")
    suspend fun findByName(name: String) : Food

    @Query("SELECT * FROM Food WHERE FoodName LIKE :name")
    suspend fun getByName(name: String) : List<Food>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(vararg foods: Food)

    @Update
    suspend fun updateFoods(vararg foods: Food)

    @Delete
    suspend fun deleteFoods(vararg foods: Food)

    @Transaction
    @Query("SELECT * FROM Food")
    suspend fun getFoodsWithRecipes() : List<FoodWithRecipes>
}