package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolAdapter
import com.mrsmart.standard.rental.ToolForRentalRequest
import com.mrsmart.standard.tool.ToolDtoSQLite
import kotlin.math.ceil


class ToolFindFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        confirmBtn = view.findViewById(R.id.ConfirmBtn)


        bluetoothManager.dataSend("Request_ToolForRentalCount_" + sharedViewModel.toolBoxId.toString())
        val toolForRentalCnt = bluetoothManager.dataReceiveSingle().toInt()
        val toolForRentalSize = 10
        for (i in 1..ceil(toolForRentalCnt.toDouble() / toolForRentalSize.toDouble()).toInt()) {
            val toolForRental = gson.toJson(ToolForRentalRequest(toolForRentalSize, i, sharedViewModel.toolBoxId, "", emptyList<Long>()))
            bluetoothManager.dataSend(toolForRental)

        }


        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools() // 재고가 포함된, 특정 toolbox의 toolList를 가져와야함
        val adapter = ToolAdapter(tools) { tool ->

        }
        confirmBtn.setOnClickListener {
            val toolList: MutableList<ToolDtoSQLite> = mutableListOf()
            for (tool: ToolDtoSQLite in adapter.getSelectedTools()) {
                toolList.add(tool) // sharedViewModel 의 rental_ToolList 에다가 toolList의 내용을 복사
            }
            sharedViewModel.toolList.addAll(toolList)

            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.adapter = adapter

        return view
    }
}