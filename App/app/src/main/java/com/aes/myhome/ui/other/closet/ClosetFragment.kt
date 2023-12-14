package com.aes.myhome.ui.other.closet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aes.myhome.databinding.FragmentClosetBinding

class ClosetFragment : Fragment() {

    private var _binding: FragmentClosetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ClosetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ClosetViewModel::class.java]
        _binding = FragmentClosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}