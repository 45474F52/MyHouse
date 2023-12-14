package com.aes.myhome.ui.food.recipes.showing

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Parcelable.ClassLoaderCreator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.RecipeStepAdapter
import com.aes.myhome.databinding.FragmentRecipesBinding
import com.aes.myhome.databinding.FragmentShowRecipeBinding
import com.aes.myhome.objects.RecipeStep
import com.aes.myhome.storage.database.entities.Recipe
import com.aes.myhome.ui.food.recipes.RecipesViewModel

class ShowRecipeFragment : Fragment(), IItemClickListener {

    private lateinit var _steps: List<RecipeStep>

    private var _binding: FragmentShowRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: ShowRecipeViewModel

    private lateinit var _stepsView: TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowRecipeBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[ShowRecipeViewModel::class.java]

        arguments?.let {
            it.classLoader = Recipe::class.java.classLoader
            val recipe: Recipe =
                it.getParcelable(Recipe::class.simpleName)
                    ?: throw IllegalStateException("Recipe item not found in bundle")

            _steps = recipe.steps

            val recyclerView: RecyclerView = binding.root.findViewById(R.id.recipe_steps_list)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = RecipeStepAdapter(_steps, this)

            val title: TextView = binding.root.findViewById(R.id.recipe_name_text)
            title.text = recipe.recipeName

            val time: TextView = binding.root.findViewById(R.id.recipe_time_text)
            time.text = getString(R.string.recipe_format_time, recipe.cookingTime.toDouble())

            _stepsView = binding.root.findViewById(R.id.recipe_steps_text)
            updateStepsView()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemLongClick(index: Int) { }

    // On Recipe Step Click
    override fun onItemClick(index: Int) {
        val step = _steps[index]
        step.toggle()

        updateStepsView()
    }

    private fun updateStepsView() {
        _stepsView.text = getString(R.string.recipe_format_steps, _steps.count { s -> s.isChecked() }, _steps.size)
    }
}