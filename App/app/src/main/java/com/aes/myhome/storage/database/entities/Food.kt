package com.aes.myhome.storage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Food",
    indices = [
        Index("FoodName", unique = true)
    ])
data class Food (

    @ColumnInfo(name = "FoodName") var foodName: String,
    @ColumnInfo(name = "UseByDate") var useByDate: String,
    @ColumnInfo(name = "Description") var description: String?,
    @ColumnInfo(name = "Protein") var protein: Double,
    @ColumnInfo(name = "Fat") var fat: Double,
    @ColumnInfo(name = "Carbs") var carbs: Double,
    @ColumnInfo(name = "Calories") var calories: Double,
    @ColumnInfo(name = "Quantity") var quantity: Byte

    ) {

    @PrimaryKey(autoGenerate = true) var foodId: Int = 0

    override fun toString(): String {
        return foodName
    }

}