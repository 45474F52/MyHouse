package com.aes.myhome.ui.food.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.aes.myhome.R
import com.aes.myhome.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private lateinit var _viewModel: MenuViewModel

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        _viewModel = ViewModelProvider(this)[MenuViewModel::class.java]

        val showFoodListBtn: Button = binding.root.findViewById(R.id.show_food_list_btn)
        showFoodListBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_food_menu_navToFoodShowing)
        }

        val findRecipesBtn: Button = binding.root.findViewById(R.id.find_recipes_btn)
        findRecipesBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_food_menu_navToRecipesFinding)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}