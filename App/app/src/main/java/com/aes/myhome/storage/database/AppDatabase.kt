package com.aes.myhome.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aes.myhome.storage.database.daos.FoodDAO
import com.aes.myhome.storage.database.daos.RecipeDAO
import com.aes.myhome.storage.database.daos.ScheduleEventDAO
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeFoodCrossRef
import com.aes.myhome.storage.database.entities.ScheduleEvent

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        Food::class,
        Recipe::class,
        RecipeFoodCrossRef::class,
        ScheduleEvent::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDAO(): FoodDAO
    abstract fun recipeDAO(): RecipeDAO
    abstract fun scheduleEventDAO(): ScheduleEventDAO
}