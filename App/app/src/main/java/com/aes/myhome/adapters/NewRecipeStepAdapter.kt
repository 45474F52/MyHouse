package com.aes.myhome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.objects.RecipeStep
import com.google.android.material.textfield.TextInputEditText

class NewRecipeStepAdapter(
    private val list: List<RecipeStep>,
    private val listener: IItemClickListener)
    : RecyclerView.Adapter<NewRecipeStepAdapter.ViewHolder>()
{

    inner class ViewHolder(stepView: View) : RecyclerView.ViewHolder(stepView), View.OnClickListener
    {

        private val _stepTextInput: TextInputEditText =
            stepView.findViewById(R.id.new_recipe_step_input)
        private val _removeStepImgBtn: ImageButton =
            stepView.findViewById(R.id.remove_recipe_step_imgBtn)

        init {
            _stepTextInput.doOnTextChanged { text, _, _, _ ->
                list[adapterPosition].text = text.toString()
            }
            _removeStepImgBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val index = adapterPosition

            if (index != -1) {
                list[index].text = ""
                listener.onItemClick(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recipe_step_new, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
}