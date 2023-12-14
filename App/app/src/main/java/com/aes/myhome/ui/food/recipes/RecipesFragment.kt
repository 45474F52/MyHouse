package com.aes.myhome.ui.food.recipes

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.RecipeStepAdapter
import com.aes.myhome.adapters.RecipesAdapter
import com.aes.myhome.databinding.FragmentRecipesBinding
import com.aes.myhome.objects.RecipeStep
import com.aes.myhome.storage.database.entities.Recipe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment(), IItemClickListener {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: RecipesViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

        val adapter = RecipesAdapter(_viewModel.recipeList, this)

        val recycler = binding.root.findViewById<RecyclerView>(R.id.recipes_list)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

        _viewModel.updateRecipes()

        ItemTouchHelper(
            ItemTouchCallback(
                0,
                swipeDirs = ItemTouchHelper.RIGHT,
                list = _viewModel.recipeList,
                recycler = recycler,
                adapter = adapter,
                onDelete = { index: Int, _: Recipe ->
                    _viewModel.requestToDelete(index)
                    //adapter.notifyItemRemoved(index)
                },
                onUndo = { _: Int, _: Recipe ->
                    _viewModel.undoDeletion()
                    //adapter.notifyDataSetChanged()
                },
                onDismissed = {
                    _viewModel.confirmDeletion(it)
                }
            )
        ).attachToRecyclerView(recycler)

        val addRecipeBtn: Button = binding.root.findViewById(R.id.add_recipe_btn)
        addRecipeBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_food_recipes_navToCreation)
        }

        val clearRecipesBtn: Button = binding.root.findViewById(R.id.clear_recipe_list_btn)
        clearRecipesBtn.setOnClickListener {
            val tmp = _viewModel.count.value!!
            _viewModel.clear()
            adapter.notifyItemRangeRemoved(0, tmp)
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            clearRecipesBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        adapter.notifyItemRangeInserted(0, _viewModel.count.value!!)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemLongClick(index: Int) { }

    // On Recipe Preview Click
    override fun onItemClick(index: Int) {
        val item = _viewModel.getRecipe(index)

        val args = Bundle()
        args.classLoader = Recipe::class.java.classLoader
        args.putParcelable(Recipe::class.simpleName, item)
        Navigation.findNavController(requireView()).navigate(R.id.nav_food_recipes_navToShowing, args)
    }

}