package com.aes.myhome.ui.home

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.aes.myhome.R
import com.aes.myhome.databinding.FragmentHomeBinding
import com.aes.myhome.storage.json.JsonDataSerializer
import kotlinx.serialization.Serializable

class HomeFragment : Fragment(), ChooseFastViewDialog.ICallbackReceiver {

    private val _fileName = "fast_views.json"

    @Serializable
    private data class CardsMap(var number: Int, var resourceId: Int, var title: String)

    private lateinit var _serializer: JsonDataSerializer

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var _array: TypedArray

    private lateinit var _titleText: TextView

    private lateinit var _firstBtn: Button
    private lateinit var _secondBtn: Button
    private lateinit var _thirdBtn: Button
    private lateinit var _fourthBtn: Button

    private lateinit var _clearBtn: Button

    private var _clickedCardNumber = 0

    private var _cardsMap = arrayListOf<CardsMap>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        _array = resources.obtainTypedArray(R.array.fast_views_values)

        _serializer = JsonDataSerializer(requireContext(), JsonDataSerializer.StorageType.INTERNAL)

        _cardsMap = _serializer.deserialize("", _fileName) ?: arrayListOf()

        _titleText = binding.root.findViewById(R.id.home_title_tv)

        _firstBtn = binding.root.findViewById(R.id.home_card_first)
        _secondBtn = binding.root.findViewById(R.id.home_card_second)
        _thirdBtn = binding.root.findViewById(R.id.home_card_third)
        _fourthBtn = binding.root.findViewById(R.id.home_card_fourth)

        _clearBtn = binding.root.findViewById(R.id.home_clear_btn)

        if (_cardsMap.size >= 1) {
            _titleText.visibility = View.GONE

            _firstBtn.text = _cardsMap[0].title

            if (_cardsMap.size >= 2) {
                _secondBtn.text = _cardsMap[1].title

                if (_cardsMap.size >= 3) {
                    _thirdBtn.text = _cardsMap[2].title

                    if (_cardsMap.size >= 4) {
                        _fourthBtn.text = _cardsMap[3].title
                    }
                }
            }
        }
        else {
            _clearBtn.visibility = View.GONE
        }

        _firstBtn.setOnClickListener {
            _clickedCardNumber = 1

            if (_cardsMap.size >= _clickedCardNumber) {
                val id = _cardsMap[0].resourceId
                navigateTo(id)
            }
            else {
                showChooseDialog()
            }
        }

        _secondBtn.setOnClickListener {
            _clickedCardNumber = 2

            if (_cardsMap.size >= _clickedCardNumber) {
                val id = _cardsMap[_clickedCardNumber - 1].resourceId
                navigateTo(id)
            }
            else {
                showChooseDialog()
            }
        }

        _thirdBtn.setOnClickListener {
            _clickedCardNumber = 3

            if (_cardsMap.size >= _clickedCardNumber) {
                val id = _cardsMap[_clickedCardNumber - 1].resourceId
                navigateTo(id)
            }
            else {
                showChooseDialog()
            }
        }

        _fourthBtn.setOnClickListener {
            _clickedCardNumber = 4

            if (_cardsMap.size >= _clickedCardNumber) {
                val id = _cardsMap[_clickedCardNumber - 1].resourceId
                navigateTo(id)
            }
            else {
                showChooseDialog()
            }
        }

        _clearBtn.setOnClickListener {
            _clickedCardNumber = 0
            _titleText.visibility = View.VISIBLE
            _clearBtn.visibility = View.GONE
            _firstBtn.text = ""
            _secondBtn.text = ""
            _thirdBtn.text = ""
            _fourthBtn.text = ""
            _cardsMap.clear()
            serializeMap()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        _cardsMap.add(CardsMap(_clickedCardNumber, id, title))
        serializeMap()
    }

    private fun showChooseDialog() {
        val dialog = ChooseFastViewDialog(this)
        dialog.show(requireActivity().supportFragmentManager, ChooseFastViewDialog.TAG)
    }

    private fun navigateTo(id: Int) {
        Navigation.findNavController(requireView()).navigate(id)
    }

    private fun serializeMap() {
        _serializer.serialize(_cardsMap, "", _fileName)
    }
}