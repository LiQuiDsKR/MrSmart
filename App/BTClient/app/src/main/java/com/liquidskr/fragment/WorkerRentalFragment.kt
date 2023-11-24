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

class WorkerRentalFragment() : Fragment() {
    lateinit var leaderSearchBtn: ImageButton
    lateinit var addToolBtn: ImageButton
    lateinit var selectAllBtn: ImageButton
    lateinit var confirmBtn: ImageButton
    lateinit var clearBtn: ImageButton

    lateinit var workerName: TextView
    lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView

    var worker: MembershipSQLite? = null
    var leader: MembershipSQLite? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental, container, false)


        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        selectAllBtn = view.findViewById(R.id.SelectAllBtn)
        confirmBtn = view.findViewById(R.id.ConfirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)

        workerName = view.findViewById(R.id.BorrowerName)
        leaderName = view.findViewById(R.id.LeaderName)

        recyclerView = view.findViewById(R.id.ManagerLobby_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        worker = sharedViewModel.loginWorker
        workerName.text = sharedViewModel.loginWorker.name
        leader = sharedViewModel.leader
        leaderName.text = sharedViewModel.leader.name

        Log.d("asdf", worker.toString())
        Log.d("asdf", sharedViewModel.worker.toString())

        leaderSearchBtn.setOnClickListener {
            val bundle = Bundle()
            val fragment = MembershipFindFragment.newInstance(2) // type = 2
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                //.addToBackStack(null)
                .commit()
        }
        addToolBtn.setOnClickListener {
            val fragment = ToolFindFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
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
                    // Log.d("test",  gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, 5222,rentalRequestToolFormDtoList.toList())))
                    if (!(worker!!.code.equals(""))) {
                        if (!(leader!!.code.equals(""))) {
                            val rentalRequestSheet = gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, 5222 ,rentalRequestToolFormDtoList.toList()))
                            sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
                            sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
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
            sharedViewModel.toolList.clear()
            val adapter = RentalToolAdapter(sharedViewModel.toolList)
            recyclerView.adapter = adapter
        }

        val adapter = RentalToolAdapter(sharedViewModel.toolList)
        recyclerView.adapter = adapter

        return view
    }
    companion object {
        fun newInstance(): WorkerRentalFragment {
            val fragment = WorkerRentalFragment()
            return fragment
        }
    }
}