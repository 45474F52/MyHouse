package com.aes.myhome.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.objects.RecipeStep

class RecipeStepAdapter(private val list: List<RecipeStep>, private val listener: IItemClickListener)
    : RecyclerView.Adapter<RecipeStepAdapter.ViewHolder>() {

    inner class ViewHolder(stepView: View) : RecyclerView.ViewHolder(stepView), View.OnClickListener {
        val stepTextView: TextView = stepView.findViewById(R.id.recipe_step_text)
        val checkBox: ImageButton = stepView.findViewById(R.id.recipe_step_checkbox)

        init {
            stepView.setOnClickListener(this)
            checkBox.setOnClickListener(this)
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
            .inflate(R.layout.item_recipe_step, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        item.listener = { isChecked ->
            if (isChecked) {
                holder.checkBox.setImageResource(R.drawable.check_circle)
                holder.stepTextView.paintFlags = holder.stepTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else {
                holder.checkBox.setImageResource(R.drawable.unchecked_circle)
                holder.stepTextView.paintFlags = holder.stepTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        holder.stepTextView.text = item.text
    }

}