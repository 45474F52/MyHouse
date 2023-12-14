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
    version = 3,
    exportSchema = true,
    entities = [
        Food::class,
        Recipe::class,
        RecipeFoodCrossRef::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AppDatabase.AppDatabaseAutoMigration::class)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDAO(): FoodDAO
    abstract fun recipeDAO(): RecipeDAO

    @RenameColumn.Entries(
        RenameColumn(
            tableName = "Food",
            fromColumnName = "id",
            toColumnName = "foodId"
        )
    )
    @DeleteColumn.Entries(
        DeleteColumn(
            tableName = "Food",
            columnName = "id"
        )
    )
    class AppDatabaseAutoMigration : AutoMigrationSpec
}