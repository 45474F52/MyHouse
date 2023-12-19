package com.aes.myhome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.DIHandler
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.Food

class FoodAdapter(private val list: List<Food>, private val listener: IItemClickListener)
    : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = list[position]

        holder.foodIdView.text = food.foodId.toString()

        holder.foodNameView.text = food.foodName
        holder.useByDateView.text = DIHandler.getResources().getString(R.string.food_format_useByDate, food.useByDate)
        holder.descriptionView.text = food.description
        holder.proteinView.text = DIHandler.getResources().getString(R.string.food_format_protein, food.protein)
        holder.fatView.text = DIHandler.getResources().getString(R.string.food_format_fat, food.fat)
        holder.carbsView.text = DIHandler.getResources().getString(R.string.food_format_carbs, food.carbs)
        holder.caloriesView.text = DIHandler.getResources().getString(R.string.food_format_calories, food.calories)
        holder.quantityView.text = DIHandler.getResources().getString(R.string.food_format_quantity, food.quantity)

        holder.layout.setOnClickListener {
            holder.container.visibility =
                if (holder.container.visibility != View.VISIBLE) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(foodView: View) : RecyclerView.ViewHolder(foodView),
        View.OnLongClickListener
    {

        val foodIdView: TextView = foodView.findViewById(R.id.food_id_text)

        val foodNameView: TextView = foodView.findViewById(R.id.food_name_text)
        val useByDateView: TextView = foodView.findViewById(R.id.food_useByDate_text)
        val descriptionView: TextView = foodView.findViewById(R.id.food_description_text)
        val proteinView: TextView = foodView.findViewById(R.id.food_protein_text)
        val fatView: TextView = foodView.findViewById(R.id.food_fat_text)
        val carbsView: TextView = foodView.findViewById(R.id.food_carbs_text)
        val caloriesView: TextView = foodView.findViewById(R.id.food_calories_text)
        val quantityView: TextView = foodView.findViewById(R.id.food_quantity_text)

        val container: LinearLayout = foodView.findViewById(R.id.food_info_container)
        val layout: LinearLayout = foodView.findViewById(R.id.item_food_layout)

        init {
            foodView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val index = adapterPosition

            if (index != -1) {
                listener.onItemLongClick(index)
                return true
            }

            return false
        }

    }
}