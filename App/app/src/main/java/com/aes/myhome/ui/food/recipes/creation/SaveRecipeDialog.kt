package com.aes.myhome.ui.food.recipes.creation

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.CheckableFoodAdapter
import com.aes.myhome.objects.CheckableText
import java.io.Serializable

class SaveRecipeDialog(
    private val receiver: ICallbackReceiver,
    private val foods: List<CheckableText>
) : DialogFragment(), IItemClickListener
{

    private var _foodsVisible = false

    private val _checkableItems = mutableListOf<CheckableText>()

    interface ICallbackReceiver : Serializable {
        fun onCreateFood(foodName: String)

        fun onPositive(
            recipeName: String,
            cookingTime: Double,
            image: Uri,
            foods: List<CheckableText>)
    }

    companion object {
        val TAG = SaveRecipeDialog::class.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val dialogView: View = inflater.inflate(R.layout.dialog_save_recipe, null)

            val posBtn: Button = dialogView.findViewById(R.id.dialog_positive_btn)
            val negBtn: Button = dialogView.findViewById(R.id.dialog_negative_btn)

            val recyclerView: RecyclerView = dialogView.findViewById(R.id.used_recipes_list)
            val adapter = CheckableFoodAdapter(foods, this)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val container: LinearLayout = dialogView.findViewById(R.id.used_recipes_container)

            val foodName: EditText = dialogView.findViewById(R.id.new_food_name_edit)
            val addFood: ImageButton = dialogView.findViewById(R.id.add_food_to_recipe_btn)
            addFood.setOnClickListener {
                if (foodName.text.isNotBlank()) {
                    receiver.onCreateFood(foodName.text.toString())
                    foodName.text.clear()
                    foodName.clearFocus()
                    adapter.notifyItemInserted(foods.size - 1)
                    foods.last().toggle()
                }
            }

            val usedRecipesText: TextView = dialogView.findViewById(R.id.used_recipes_text)
            usedRecipesText.setOnClickListener {
                _foodsVisible = !_foodsVisible
                container.visibility = if (_foodsVisible) View.VISIBLE else View.GONE
            }

            val recipeName: EditText = dialogView.findViewById(R.id.recipe_name_edit)
            val cookingTime: EditText = dialogView.findViewById(R.id.recipe_cookingTime_edit)
            val loadImageBtn: Button = dialogView.findViewById(R.id.load_recipe_image_btn)
            val image: ImageView = dialogView.findViewById(R.id.recipe_preview_image_iv)
            var path: Uri = Uri.EMPTY

            val pickMedia =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    path = uri
                    image.setImageURI(uri)
                }
            }

            loadImageBtn.setOnClickListener {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            posBtn.setOnClickListener {
                val name = recipeName.text.toString()
                val time = cookingTime.text.toString().toDoubleOrNull()

                if (name.isBlank()) {
                    recipeName.setError(
                        getString(R.string.error_empty_name),
                        AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                    recipeName.requestFocus()
                    return@setOnClickListener
                }

                if (time == null || time < .0) {
                    cookingTime.setError(
                        getString(R.string.error_timeFormat),
                        AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                    cookingTime.requestFocus()
                    return@setOnClickListener
                }

                receiver.onPositive(name, time, path, _checkableItems)
                dismiss()
            }

            negBtn.setOnClickListener {
                dismiss()
            }

            builder
                .setView(dialogView)
                .setTitle(getString(R.string.dialog_saveRecipe_title))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onItemLongClick(index: Int) { }

    override fun onItemClick(index: Int) {
        val item = foods[index]
        item.toggle()

        if (item.isChecked()) {
            _checkableItems.add(item)
        } else {
            _checkableItems.remove(item)
        }
    }

}