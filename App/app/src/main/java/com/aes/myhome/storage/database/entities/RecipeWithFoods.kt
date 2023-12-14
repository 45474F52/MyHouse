package com.aes.myhome.storage.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Объект "Рецепт" с используемыми продуктами
 */
data class RecipeWithFoods(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "foodId",
        associateBy = Junction(RecipeFoodCrossRef::class)
    )
    val foods: List<Food>
)
