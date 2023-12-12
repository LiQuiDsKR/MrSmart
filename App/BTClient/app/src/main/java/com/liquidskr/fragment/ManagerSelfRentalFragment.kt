package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalToolAdapter
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import com.mrsmart.standard.tool.ToolDtoSQLite

class ManagerSelfRentalFragment() : Fragment() {
    private lateinit var workerSearchBtn: ImageButton
    private lateinit var leaderSearchBtn: ImageButton
    private lateinit var addToolBtn: ImageButton
    private lateinit var selectAllBtn: ImageButton
    private lateinit var confirmBtn: ImageButton
    private lateinit var clearBtn: ImageButton

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView

    var worker: MembershipSQLite? = null
    var leader: MembershipSQLite? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_self_rental, container, false)

        workerSearchBtn = view.findViewById(R.id.BorrowerSearchBtn)
        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        selectAllBtn = view.findViewById(R.id.SelectAllBtn)
        confirmBtn = view.findViewById(R.id.ConfirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)

        workerName = view.findViewById(R.id.BorrowerName)
        leaderName = view.findViewById(R.id.LeaderName)

        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        worker = sharedViewModel.worker
        workerName.text = sharedViewModel.worker.name
        leader = sharedViewModel.leader
        leaderName.text = sharedViewModel.leader.name

        Log.d("asdf", worker.toString())
        Log.d("asdf", sharedViewModel.worker.toString())

        workerSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(1) // type = 1
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                //.addToBackStack(null)
                .commit()
        }

        leaderSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(2) // type = 2
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                //.addToBackStack(null)
                .commit()
        }
        addToolBtn.setOnClickListener {
            val fragment = ToolFindFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        selectAllBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalToolAdapter) {
                    adapter.selectAllTools(recyclerView, adapter)
                }
            }
        }
        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalToolAdapter) {
                    val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                    for (tool: ToolDtoSQLite in adapter.selectedToolsToRental) {
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.tools.indexOf(tool)) as? RentalToolAdapter.RentalToolViewHolder
                        val toolCount = holder?.toolCount?.text?.toString()?.toIntOrNull() ?: 0
                        rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(tool.id, toolCount))
                    }
                    Log.d("test",  gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, 5222,rentalRequestToolFormDtoList.toList())))
                    if (!(worker!!.code.equals(""))) {
                        if (!(leader!!.code.equals(""))) {
                            val rentalRequestSheet = gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, sharedViewModel.toolBoxId ,rentalRequestToolFormDtoList.toList()))
                            requireActivity().supportFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "리더를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "작업자를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        clearBtn.setOnClickListener {
            sharedViewModel.rentalRequestToolList.clear()
            val adapter = RentalToolAdapter(sharedViewModel.rentalRequestToolList)
            recyclerView.adapter = adapter
        }

        val adapter = RentalToolAdapter(sharedViewModel.rentalRequestToolList)
        recyclerView.adapter = adapter

        return view
    }
    companion object {
        fun newInstance(): ManagerSelfRentalFragment {
            val fragment = ManagerSelfRentalFragment()
            return fragment
        }
    }
}