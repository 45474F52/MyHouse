package com.aes.myhome.storage.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.aes.myhome.storage.database.daos.FoodDAO
import com.aes.myhome.storage.database.daos.RecipeDAO
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeFoodCrossRef

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        Food::class,
        Recipe::class,
        RecipeFoodCrossRef::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDAO(): FoodDAO
    abstract fun recipeDAO(): RecipeDAO
}