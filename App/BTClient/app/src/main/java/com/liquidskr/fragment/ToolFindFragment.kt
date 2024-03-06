package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolAdapter
import com.mrsmart.standard.tool.ToolSQLite


class ToolFindFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManagerOld: BluetoothManager_Old

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManagerOld = BluetoothManager_Old(requireContext(), requireActivity())
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_list, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        confirmBtn = view.findViewById(R.id.confirmBtn)


        val databaseHelper = DatabaseHelper.getInstance()
        val tools: List<ToolSQLite> = databaseHelper.getAllTools() // 재고가 포함된, 특정 toolbox의 toolList를 가져와야함 >> X
        val adapter = ToolAdapter(tools) {

        }
        confirmBtn.setOnClickListener {
            printFragmentStack()
            val toolIdList: MutableList<Long> = mutableListOf()
            for (tool: ToolSQLite in adapter.getSelectedTools()) {
                toolIdList.add(tool.id) // sharedViewModel 의 rental_ToolList 에다가 toolList의 내용을 복사
            }
            sharedViewModel.rentalRequestToolIdList.clear()
            sharedViewModel.rentalRequestToolIdList.addAll(toolIdList)
            requireActivity().supportFragmentManager.popBackStack()
        }
        searchBtn.setOnClickListener {
            filterByName(adapter, tools, editTextName.text.toString())
        }

        recyclerView.adapter = adapter

        return view
    }
    fun filterByName(adapter: ToolAdapter, tools: List<ToolSQLite>, keyword: String) {
        val newList: MutableList<ToolSQLite> = mutableListOf()
        for (tool in tools) {
            if (keyword in tool.name) {
                newList.add(tool)
            }
        }
        adapter.updateList(newList)
    }
    fun printFragmentStack() {
        val activity = requireActivity()
        val fragmentManager = activity.supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackCount) {
            val fragmentName = fragmentManager.getBackStackEntryAt(i).javaClass.simpleName
            Log.d("FragmentStack", "position : ${fragmentName}")
        }
    }
}