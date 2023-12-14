package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.R

class SettingsFragment() : Fragment() {
    lateinit var toolBoxSpinner: Spinner
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        toolBoxSpinner = view.findViewById(R.id.spinner)
        var toolboxArray = arrayOf("선강정비1실", "선강정비2실", "선강정비3실", "선강정비4실", "선강정비5실")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, toolboxArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toolBoxSpinner.adapter = adapter
        return view

        toolBoxSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                when(position) {
                    1-> sharedViewModel.toolBoxId = 5222
                    2-> sharedViewModel.toolBoxId = 5223
                    3-> sharedViewModel.toolBoxId = 5224
                    4-> sharedViewModel.toolBoxId = 5225
                    5-> sharedViewModel.toolBoxId = 5226
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // 선택된 것이 없을 때의 동작을 여기에 추가하세요.
            }
        }
    }
}