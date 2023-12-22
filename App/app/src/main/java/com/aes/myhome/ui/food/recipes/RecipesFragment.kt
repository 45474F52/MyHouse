package com.aes.myhome.ui.food.recipes

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.ItemTouchCallback
import com.aes.myhome.R
import com.aes.myhome.adapters.RecipesAdapter
import com.aes.myhome.databinding.FragmentRecipesBinding
import com.aes.myhome.storage.database.entities.Recipe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class RecipesFragment : Fragment(),
    IItemClickListener,
    ItemTouchCallback.Receiver<Recipe>,
    SearchView.OnQueryTextListener
{

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: RecipesViewModel
    private lateinit var _adapter: RecipesAdapter

    private val _filteredRecipes = mutableListOf<Recipe>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[RecipesViewModel::class.java]
        _viewModel.loadRecipes {
            _filteredRecipes.clear()
            _filteredRecipes.addAll(_viewModel.recipes.value!!)

            _adapter = RecipesAdapter(_filteredRecipes, this)

            val recycler = binding.root.findViewById<RecyclerView>(R.id.recipes_list)
            recycler.adapter = _adapter
            recycler.layoutManager = LinearLayoutManager(context)

            ItemTouchHelper(
                ItemTouchCallback(
                    0,
                    swipeDirs = ItemTouchHelper.RIGHT,
                    list = _filteredRecipes,
                    recycler = recycler,
                    this
                )
            ).attachToRecyclerView(recycler)
        }

        val addRecipeBtn: FloatingActionButton = binding.root.findViewById(R.id.add_recipe_btn)
        addRecipeBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_food_recipes_navToCreation)
        }

        val clearRecipesBtn: ImageButton = binding.root.findViewById(R.id.clear_recipe_list_btn)
        clearRecipesBtn.setOnClickListener {
            val tmp = _viewModel.count.value!!
            _viewModel.clear()
            _filteredRecipes.clear()
            _adapter.notifyItemRangeRemoved(0, tmp)
        }

        val search: SearchView = binding.root.findViewById(R.id.search_recipes_view)
        search.setOnQueryTextListener(this)
        search.setOnCloseListener {
            _filteredRecipes.clear()
            _filteredRecipes.addAll(_viewModel.recipes.value!!)
            _adapter.notifyDataSetChanged()

            return@setOnCloseListener true
        }

        _viewModel.count.observe(viewLifecycleOwner) {
            clearRecipesBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemLongClick(index: Int) { }

    // On Recipe Preview Click
    override fun onItemClick(index: Int) {
        val item = _filteredRecipes[index]

        val args = Bundle()
        args.classLoader = Recipe::class.java.classLoader
        args.putParcelable(Recipe::class.simpleName, item)
        Navigation.findNavController(requireView()).navigate(R.id.nav_food_recipes_navToShowing, args)
    }

    // Receive ItemTouchCallback

    override fun onDelete(index: Int, item: Recipe) {
        _viewModel.requestToDelete(item)
        _filteredRecipes.removeAt(index)
        _adapter.notifyItemRemoved(index)
    }

    override fun onUndo(index: Int, item: Recipe) {
        _viewModel.undoDeletion(item)
        _filteredRecipes.add(index, item)
        _adapter.notifyItemInserted(index)
    }

    override fun onDismissed(item: Recipe) {
        _filteredRecipes.remove(item)
        _viewModel.confirmDeletion(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onQueryTextSubmit(query: String?): Boolean {
        _filteredRecipes.clear()

        if (!query.isNullOrBlank()) {
            val filter = query.trim().lowercase(Locale("ru-RU"))

            for (recipe in _viewModel.recipes.value!!) {
                if (recipe.recipeName.lowercase(Locale("ru-RU")).contains(filter)) {
                    _filteredRecipes.add(recipe)
                }
            }
        }
        else {
            _filteredRecipes.addAll(_viewModel.recipes.value!!)
        }

        _adapter.notifyDataSetChanged()

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}