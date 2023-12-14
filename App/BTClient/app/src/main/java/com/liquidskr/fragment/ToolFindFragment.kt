package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.mrsmart.standard.tool.ToolDtoSQLite


class ToolFindFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_list, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        confirmBtn = view.findViewById(R.id.ConfirmBtn)

        /* 아래는 블루투스로 각 정비실의 공구 리스트 불러오는 과정
        bluetoothManager.dataSend("Request_ToolForRentalCount_" + sharedViewModel.toolBoxId.toString())
        val toolForRentalCnt = bluetoothManager.dataReceiveSingle().toInt()
        val toolForRentalSize = 10
        for (i in 1..ceil(toolForRentalCnt.toDouble() / toolForRentalSize.toDouble()).toInt()) {
            val toolForRental = gson.toJson(ToolForRentalRequest(toolForRentalSize, i, sharedViewModel.toolBoxId, "", emptyList<Long>()))
            bluetoothManager.dataSend(toolForRental)
        }
        //받게 되는 것 : 해당 정비실의 공구 리스트 (미구현)
        */


        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools() // 재고가 포함된, 특정 toolbox의 toolList를 가져와야함
        val adapter = ToolAdapter(tools) {

        }
        confirmBtn.setOnClickListener {
            val toolList: MutableList<ToolDtoSQLite> = mutableListOf()
            for (tool: ToolDtoSQLite in adapter.getSelectedTools()) {
                toolList.add(tool) // sharedViewModel 의 rental_ToolList 에다가 toolList의 내용을 복사
            }
            sharedViewModel.rentalRequestToolList.addAll(toolList)

            requireActivity().supportFragmentManager.popBackStack()
        }
        searchBtn.setOnClickListener {
            filterByName(adapter, tools, editTextName.text.toString())
        }

        recyclerView.adapter = adapter

        return view
    }
    fun filterByName(adapter: ToolAdapter, tools: List<ToolDtoSQLite>, keyword: String) {
        val newList: MutableList<ToolDtoSQLite> = mutableListOf()
        for (membership in tools) {
            if (keyword in membership.name) {
                newList.add(membership)
            }
        }
        adapter.updateList(newList)
    }
}