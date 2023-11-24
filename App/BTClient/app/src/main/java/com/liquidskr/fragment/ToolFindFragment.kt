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
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolAdapter
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolDtoSQLite


class ToolFindFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: ImageButton

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tool_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        confirmBtn = view.findViewById(R.id.ConfirmBtn)

        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()
        val adapter = ToolAdapter(tools) { tool ->

        }
        confirmBtn.setOnClickListener {
            val toolList: MutableList<ToolDtoSQLite> = mutableListOf()
            for (tool: ToolDtoSQLite in adapter.getSelectedTools()) {
                toolList.add(tool)
                // sharedViewModel 의 rental_ToolList 에다가 toolList의 내용을 복사
            }
            sharedViewModel.toolList.addAll(toolList)

            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.adapter = adapter

        return view
    }
}