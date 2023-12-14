package com.aes.myhome.storage.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.aes.myhome.objects.RecipeStep
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "Recipe")
data class Recipe(

    @ColumnInfo(name = "RecipeName") var recipeName: String,
    @ColumnInfo(name = "CreationDate") var creationDate: String,
    @ColumnInfo(name = "Description") var description: String,
    @ColumnInfo(name = "CookingTime") var cookingTime: String,
    @ColumnInfo(name = "Image") var image: String,

) : Parcelable {

    @PrimaryKey(autoGenerate = true) var recipeId: Int = 0
    @Ignore val stepsDelimiter: Char = '#'
    @Ignore @Transient var steps: List<RecipeStep> = arrayListOf()

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
        recipeId = parcel.readInt()
    }

    init {
        if (description.isNotEmpty()) {
            steps = description
                .split(stepsDelimiter)
                .map { t -> RecipeStep(t) }
                .filter { s -> s.text.isNotBlank() }
        }
    }

    fun convertStepsToDescription() {
        if (steps.isNotEmpty()) {
            val builder = StringBuilder()
            for (step in steps) {
                if (step.text.isNotBlank()) {
                    builder.append("${stepsDelimiter}${step.text}")
                }
            }
            description = builder.toString()
        }
        else {
            throw Exception("Steps count must be greater then 0")
        }
    }

    override fun toString(): String {
        return recipeName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recipeName)
        parcel.writeString(creationDate)
        parcel.writeString(description)
        parcel.writeString(cookingTime)
        parcel.writeString(image)
        parcel.writeInt(recipeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
