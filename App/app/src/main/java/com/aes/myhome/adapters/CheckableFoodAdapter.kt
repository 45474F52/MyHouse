package com.aes.myhome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.objects.CheckableText

class CheckableFoodAdapter(
    private val list: List<CheckableText>,
    private val listener: IItemClickListener)
    : RecyclerView.Adapter<CheckableFoodAdapter.ViewHolder>()
{
    inner class ViewHolder(foodView: View) : RecyclerView.ViewHolder(foodView),
        View.OnClickListener
    {
        val foodName: TextView = foodView.findViewById(R.id.food_name_text)
        val imageBtn: ImageButton = foodView.findViewById(R.id.item_food_checkbox)

        init {
            foodView.setOnClickListener(this)
            imageBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val index = adapterPosition

            if (index != -1) {
                listener.onItemClick(index)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_food_checkable, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        item.listener = { isChecked ->
            if (isChecked) {
                holder.imageBtn.setImageResource(R.drawable.check_circle)
            }
            else {
                holder.imageBtn.setImageResource(R.drawable.unchecked_circle)
            }
        }

        holder.foodName.text = item.text
    }
}