package com.aes.myhome.ui.home

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.aes.myhome.R
import com.aes.myhome.databinding.FragmentHomeBinding
import com.aes.myhome.objects.CardsMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), ChooseFastViewDialog.ICallbackReceiver {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: HomeViewModel

    private lateinit var _array: TypedArray

    private lateinit var _titleText: TextView

    private lateinit var _firstBtn: Button
    private lateinit var _secondBtn: Button
    private lateinit var _thirdBtn: Button
    private lateinit var _fourthBtn: Button

    private lateinit var _clearBtn: Button

    private var _clickedCardNumber = 0
    private var _update = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _array = resources.obtainTypedArray(R.array.fast_views_values)

        _titleText = binding.root.findViewById(R.id.home_title_tv)

        _firstBtn = binding.root.findViewById(R.id.home_card_first)
        _secondBtn = binding.root.findViewById(R.id.home_card_second)
        _thirdBtn = binding.root.findViewById(R.id.home_card_third)
        _fourthBtn = binding.root.findViewById(R.id.home_card_fourth)

        _clearBtn = binding.root.findViewById(R.id.home_clear_btn)

        _viewModel.cardsMap.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                _titleText.visibility = View.GONE

                _firstBtn.text = it[0].title

                if (it.size >= 2) {
                    _secondBtn.text = it[1].title

                    if (it.size >= 3) {
                        _thirdBtn.text = it[2].title

                        if (it.size >= 4) {
                            _fourthBtn.text = it[3].title
                        }
                    }
                }
            } else {
                _clearBtn.visibility = View.GONE
            }
        }

        _firstBtn.setOnClickListener {
            _clickedCardNumber = 1

            if (_viewModel.cardsMap.value!!.size >= _clickedCardNumber) {
                val id = _viewModel.cardsMap.value!![0].resourceId
                navigateTo(id)
            } else {
                showChooseDialog()
            }
        }

        _secondBtn.setOnClickListener {
            _clickedCardNumber = 2

            if (_viewModel.cardsMap.value!!.size >= _clickedCardNumber) {
                val id = _viewModel.cardsMap.value!![_clickedCardNumber - 1].resourceId
                navigateTo(id)
            } else {
                showChooseDialog()
            }
        }

        _thirdBtn.setOnClickListener {
            _clickedCardNumber = 3

            if (_viewModel.cardsMap.value!!.size >= _clickedCardNumber) {
                val id = _viewModel.cardsMap.value!![_clickedCardNumber - 1].resourceId
                navigateTo(id)
            } else {
                showChooseDialog()
            }
        }

        _fourthBtn.setOnClickListener {
            _clickedCardNumber = 4

            if (_viewModel.cardsMap.value!!.size >= _clickedCardNumber) {
                val id = _viewModel.cardsMap.value!![_clickedCardNumber - 1].resourceId
                navigateTo(id)
            } else {
                showChooseDialog()
            }
        }

        _firstBtn.setOnLongClickListener {
            _clickedCardNumber = 1
            _update = _viewModel.cardsMap.value!!.size >= _clickedCardNumber
            showChooseDialog()
            true
        }

        _secondBtn.setOnLongClickListener {
            _clickedCardNumber = 2
            _update = _viewModel.cardsMap.value!!.size >= _clickedCardNumber
            showChooseDialog()
            true
        }

        _thirdBtn.setOnLongClickListener {
            _clickedCardNumber = 3
            _update = _viewModel.cardsMap.value!!.size >= _clickedCardNumber
            showChooseDialog()
            true
        }

        _fourthBtn.setOnLongClickListener {
            _clickedCardNumber = 4
            _update = _viewModel.cardsMap.value!!.size >= _clickedCardNumber
            showChooseDialog()
            true
        }

        _clearBtn.setOnClickListener {
            _clickedCardNumber = 0
            _titleText.visibility = View.VISIBLE
            _clearBtn.visibility = View.GONE
            _firstBtn.text = ""
            _secondBtn.text = ""
            _thirdBtn.text = ""
            _fourthBtn.text = ""
            _viewModel.clear()
            _viewModel.save()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNegative() {
        if (_update) {
            _update = false
        }
    }

    override fun onPositive(title: String, index: Int) {
        _titleText.visibility = View.GONE
        val id = _array.getResourceId(index, -1)

        when(_clickedCardNumber) {
            1 -> _firstBtn.text = title
            2 -> _secondBtn.text = title
            3 -> _thirdBtn.text = title
            4 -> _fourthBtn.text = title
            else -> return
        }

        if (_clearBtn.visibility != View.VISIBLE) {
            _clearBtn.visibility = View.VISIBLE
        }

        if (_update) {
            _update = false
            _viewModel.update(_clickedCardNumber - 1, id, title)
        } else {
            _viewModel.add(CardsMap(_clickedCardNumber, id, title))
        }

        _viewModel.save()
    }

    private fun showChooseDialog() {
        val dialog = ChooseFastViewDialog(this)
        dialog.show(requireActivity().supportFragmentManager, ChooseFastViewDialog.TAG)
    }

    private fun navigateTo(id: Int) {
        Navigation.findNavController(requireView()).navigate(id)
    }
}