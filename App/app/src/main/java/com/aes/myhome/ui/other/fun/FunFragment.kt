package com.aes.myhome.ui.other.`fun`

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aes.myhome.databinding.FragmentFunBinding

class FunFragment : Fragment() {

    private var _binding: FragmentFunBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FunViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FunViewModel::class.java]
        _binding = FragmentFunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}