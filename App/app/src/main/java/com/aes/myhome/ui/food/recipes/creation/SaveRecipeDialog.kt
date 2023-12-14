package com.aes.myhome.ui.food.recipes.creation

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.aes.myhome.R
import java.io.Serializable

class SaveRecipeDialog(private val receiver: ICallbackReceiver) : DialogFragment() {

    interface ICallbackReceiver : Serializable {
        fun onPositive(recipeName: String, cookingTime: Double, image: Uri)
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

            val recipeName: EditText = dialogView.findViewById(R.id.recipe_name_edit)
            val cookingTime: EditText = dialogView.findViewById(R.id.recipe_cookingTime_edit)
            val loadImageBtn: Button = dialogView.findViewById(R.id.load_recipe_image_btn)
            val image: ImageView = dialogView.findViewById(R.id.recipe_preview_image_iv)
            var path: Uri = Uri.EMPTY

            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    path = uri
                    image.setImageURI(uri)
                }
            }

            loadImageBtn.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            posBtn.setOnClickListener {
                val name = recipeName.text.toString()
                val time = cookingTime.text.toString().toDoubleOrNull()

                if (name.isBlank()) {
                    recipeName.setError(
                        "Название не моежт быть пустым",
                        AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                    recipeName.requestFocus()
                    return@setOnClickListener
                }

                if (time == null || time < .0) {
                    cookingTime.setError(
                        "Время приготовления не может быть отрицательным",
                        AppCompatResources.getDrawable(requireContext(), R.drawable.error))
                    cookingTime.requestFocus()
                    return@setOnClickListener
                }

                receiver.onPositive(name, time, path)
                dismiss()
            }

            negBtn.setOnClickListener {
                dismiss()
            }

            builder
                .setView(dialogView)
                .setTitle("Сохранение рецепта")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}