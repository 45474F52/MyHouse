package com.aes.myhome.storage.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Объект "Продукт" с рецептами, в которых он используется
 */
data class FoodWithRecipes(
    @Embedded val food: Food,
    @Relation(
        parentColumn = "foodId",
        entityColumn = "recipeId",
        associateBy = Junction(RecipeFoodCrossRef::class)
    )
    val recipes: List<Recipe>
)
