package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolRegisterAdapter
import com.mrsmart.standard.tool.ToolDtoSQLite


class ToolRegisterListFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager


    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_register_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()
        val adapter = ToolRegisterAdapter(tools) { tool ->
            val fragment = ToolRegisterDetailFragment(tool.toToolDto())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }


        recyclerView.adapter = adapter

        return view
    }
}