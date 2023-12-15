package com.aes.myhome.ui.food.recipes.creation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.NewRecipeStepAdapter
import com.aes.myhome.databinding.FragmentCreateRecipeBinding
import com.aes.myhome.objects.RecipeStep
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.repositories.RecipeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class CreateRecipeFragment : Fragment(), IItemClickListener, SaveRecipeDialog.ICallbackReceiver {

    @Inject lateinit var repository: RecipeRepository

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var _saveRecipeBtn: Button

    private val _steps = arrayListOf<RecipeStep>()
    private val _adapter = NewRecipeStepAdapter(_steps, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)

        val recycler: RecyclerView = binding.root.findViewById(R.id.newRecipe_steps_list)
        recycler.adapter = _adapter
        recycler.layoutManager = LinearLayoutManager(context)

        _saveRecipeBtn = binding.root.findViewById(R.id.save_recipe_btn)
        _saveRecipeBtn.setOnClickListener {
            saveRecipeDialog()
        }

        val addStepBtn: Button = binding.root.findViewById(R.id.add_step_btn)
        addStepBtn.setOnClickListener {
            _steps.add(RecipeStep(""))
            _adapter.notifyItemInserted(_steps.size - 1)

            if (_saveRecipeBtn.visibility == View.GONE) {
                _saveRecipeBtn.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemLongClick(index: Int) {}

    override fun onItemClick(index: Int) {
        _steps.removeAt(index)
        _adapter.notifyItemRemoved(index)

        if (_steps.isEmpty()) {
            _saveRecipeBtn.visibility = View.GONE
        }
    }

    private fun saveRecipeDialog() {
        val dialog = SaveRecipeDialog(this)
        dialog.show(requireActivity().supportFragmentManager, SaveRecipeDialog.TAG)
    }

    override fun onPositive(recipeName: String, cookingTime: Double, image: Uri) {
        val recipe = Recipe(
            recipeName = recipeName,
            creationDate = repository.dateTimeFormat.format(Date()),
            description = "",
            cookingTime = cookingTime.toString(),
            image = image.toString()
        )
        recipe.steps = _steps
        recipe.convertStepsToDescription()
        saveRecipe(recipe)
        Navigation.findNavController(requireView()).navigate(R.id.nav_food_recipes_navToRecipes)
    }

    private fun saveRecipe(recipe: Recipe) = runBlocking {
        launch {
            repository.insertAll(recipe)
        }
    }

}