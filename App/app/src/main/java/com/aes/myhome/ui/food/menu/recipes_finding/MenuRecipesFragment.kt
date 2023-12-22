package com.aes.myhome.ui.food.menu.recipes_finding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.adapters.RecipesAdapter
import com.aes.myhome.databinding.FragmentMenuRecipesBinding
import com.aes.myhome.storage.database.entities.Recipe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuRecipesFragment : Fragment() {

    private var _binding: FragmentMenuRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: MenuRecipesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuRecipesBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[MenuRecipesViewModel::class.java]

        val pbContainer = binding.recipesFindingPbContainer

        val foundRecipesContainer = binding.foundRecipesContainer
        val recommendedRecipesContainer = binding.recommendedRecipesContainer

        val foundRecipesRecycler = binding.foundRecipesList
        val recommendedRecipesRecycler = binding.recommendedRecipesList

        _viewModel.foundRecipes.observe(viewLifecycleOwner) {
            val adapter = RecipesAdapter(it, FoundRecipeClickReceiver())
            foundRecipesRecycler.adapter = adapter
            foundRecipesRecycler.layoutManager = LinearLayoutManager(requireContext())

            foundRecipesContainer.visibility = View.VISIBLE
        }

        _viewModel.recommendedRecipes.observe(viewLifecycleOwner) {
            val adapter = RecipesAdapter(it, RecommendedRecipeClickReceiver())
            recommendedRecipesRecycler.adapter = adapter
            recommendedRecipesRecycler.layoutManager = LinearLayoutManager(requireContext())

            recommendedRecipesContainer.visibility = View.VISIBLE
        }

        _viewModel.loadData {
            pbContainer.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class FoundRecipeClickReceiver: IItemClickListener {
        override fun onItemLongClick(index: Int) {}

        override fun onItemClick(index: Int) {
            val item = _viewModel.foundRecipes.value!![index]
            navigateWith(item)
        }
    }

    private inner class RecommendedRecipeClickReceiver: IItemClickListener {
        override fun onItemLongClick(index: Int) {}

        override fun onItemClick(index: Int) {
            val item = _viewModel.recommendedRecipes.value!![index]
            navigateWith(item)
        }
    }

    private fun navigateWith(item: Recipe) {
        val args = Bundle()
        args.classLoader = Recipe::class.java.classLoader
        args.putParcelable(Recipe::class.simpleName, item)
        Navigation.findNavController(requireView()).navigate(R.id.nav_food_recipes_navToShowing, args)
    }
}