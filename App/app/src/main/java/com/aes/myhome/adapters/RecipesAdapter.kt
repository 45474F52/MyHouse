package com.aes.myhome.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.DIHandler
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.Recipe
import java.io.File

class RecipesAdapter(private val list: List<Recipe>, private val listener: IItemClickListener)
    : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipesAdapter.ViewHolder, position: Int) {
        val recipe = list[position]

        // TODO: Fix FileNotFoundException
//        if (recipe.image.isNotEmpty()) {
//            val path = Uri.parse(recipe.image).toString()
//            val file = File(path)
//            try {
//                file.inputStream().use {
//                    holder.imageContainerView.background = Drawable.createFromStream(it, null)
//                }
//            }
//            catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        holder.recipeNameView.text = recipe.recipeName
        holder.recipeDescriptionView.text = DIHandler.getResources()
            .getString(R.string.recipe_format_time, recipe.cookingTime.toDouble())
    }

    inner class ViewHolder(recipeView: View) : RecyclerView.ViewHolder(recipeView), View.OnClickListener {
        val imageContainerView: LinearLayout = recipeView.findViewById(R.id.recipe_image_container)
        val recipeNameView: TextView = recipeView.findViewById(R.id.recipe_name_text)
        val recipeDescriptionView: TextView = recipeView.findViewById(R.id.recipe_description_text)

        init {
            recipeView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val index = adapterPosition

            if (index != -1) {
                listener.onItemClick(index)
            }
        }
    }
}