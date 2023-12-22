package com.aes.myhome.ui.food.recipes.creation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.NewRecipeStepAdapter
import com.aes.myhome.databinding.FragmentCreateRecipeBinding
import com.aes.myhome.objects.CheckableText
import com.aes.myhome.storage.database.entities.Food
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.storage.database.entities.RecipeFoodCrossRef
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@AndroidEntryPoint
class CreateRecipeFragment : Fragment(), IItemClickListener, SaveRecipeDialog.ICallbackReceiver {

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: CreateRecipeViewModel

    private lateinit var _saveRecipeBtn: Button

    private val _steps = arrayListOf<CheckableText>()
    private val _adapter = NewRecipeStepAdapter(_steps, this)

    private lateinit var _foods: List<Food>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[CreateRecipeViewModel::class.java]

        _viewModel.foods.observe(viewLifecycleOwner) {
            _foods = it
        }

        val recycler: RecyclerView = binding.root.findViewById(R.id.newRecipe_steps_list)
        recycler.adapter = _adapter
        recycler.layoutManager = LinearLayoutManager(context)

        _saveRecipeBtn = binding.root.findViewById(R.id.save_recipe_btn)
        _saveRecipeBtn.setOnClickListener {
            showSaveRecipeDialog()
        }

        val addStepBtn: Button = binding.root.findViewById(R.id.add_step_btn)
        addStepBtn.setOnClickListener {
            _steps.add(CheckableText(""))
            val position = _steps.size - 1
            _adapter.notifyItemInserted(position)
            recycler.scrollToPosition(position)
            (recycler.layoutManager as LinearLayoutManager)
                .findViewByPosition(position)?.requestFocus()

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

    private val _checkableFoods = mutableListOf<CheckableText>()
    private fun showSaveRecipeDialog() {
        _checkableFoods.addAll(_foods.map { f -> CheckableText(f.foodName) })

        val dialog = SaveRecipeDialog(
            this@CreateRecipeFragment,
            _checkableFoods)

        dialog.show(requireActivity().supportFragmentManager, SaveRecipeDialog.TAG)
    }

    override fun onCreateFood(foodName: String) {
        _checkableFoods.add(CheckableText(foodName))

        lifecycleScope.launch {
            _viewModel.createFood(foodName)
        }
    }

    override fun onPositive(
        recipeName: String,
        cookingTime: Double,
        image: Uri,
        foods: List<CheckableText>
    ) {
        val recipe = Recipe(
            recipeName = recipeName,
            creationDate = _viewModel.formatAsRecipesDateTime(Date()),
            description = "",
            cookingTime = cookingTime.toString(),
            image = image.toString()
        )
        recipe.steps = _steps
        recipe.convertStepsToDescription()

        _viewModel.saveRecipe(foods, recipe)

        Navigation.findNavController(requireView()).navigate(R.id.nav_food_recipes_navToRecipes)
    }

}