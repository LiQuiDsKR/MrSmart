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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolFindFragment(private val selectedToolIdList : MutableList<Long>) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: LinearLayout

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private lateinit var tools : List<ToolSQLite>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tool_list, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        confirmBtn = view.findViewById(R.id.confirmBtn)

        val databaseHelper = DatabaseHelper.getInstance()
        val adapter = ToolAdapter(mutableListOf())

        //Coroutine으로 비동기 로딩 (평균 2.5초 정도 걸림)
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("ToolFindFragment", "getAllTools start")
            tools = databaseHelper.getAllTools()
            Log.d("ToolFindFragment", "getAllTools end")
            withContext(Dispatchers.Main) {
                // UI 업데이트
                adapter.updateList(tools)
                for (toolId in selectedToolIdList) {
                    adapter.selectTool(toolId)
                }
            }
        }

        confirmBtn.setOnClickListener {
            val toolIdList: MutableList<Long> = mutableListOf()
            for (tool: ToolSQLite in adapter.getSelectedTools()) {
                toolIdList.add(tool.id)
            }
            requireActivity().supportFragmentManager.setFragmentResult("toolIdList", Bundle().apply {
                putLongArray("toolIdList", toolIdList.toLongArray())
            })
            requireActivity().supportFragmentManager.popBackStack()
        }
        searchBtn.setOnClickListener {
            //filter by name
            if (tools==null) return@setOnClickListener
            val newList: MutableList<ToolSQLite> = mutableListOf()
            for (tool in tools) {
                if ( editTextName.text.toString() in tool.name) {
                    newList.add(tool)
                }
            }
            adapter.updateList(newList)
        }

        recyclerView.adapter = adapter

        Log.d("ToolFindFragment", "return view")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ToolFindFragment", "onViewCreated")
    }
}