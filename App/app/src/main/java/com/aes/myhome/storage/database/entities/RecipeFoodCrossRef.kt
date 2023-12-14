package com.aes.myhome.storage.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["foodId", "recipeId"])
data class RecipeFoodCrossRef(
    val foodId: Int,
    val recipeId: Int
)
