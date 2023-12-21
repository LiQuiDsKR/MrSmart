package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetDto
import java.lang.reflect.Type

class ManagerRentalFragment() : Fragment() {
    lateinit var searchTypeSpinner: Spinner
    lateinit var recyclerView: RecyclerView
    lateinit var selfRentalBtn: ImageButton
    lateinit var searchSheetEdit: EditText
    lateinit var sheetSearchBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager
    lateinit var rentalRequestSheetList: List<RentalRequestSheetDto>
    var selectedCategory = "리더명"

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_manager_rental, container, false)

        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)
        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        val layoutManager = LinearLayoutManager(requireContext())

        val adapter = RentalRequestSheetAdapter(emptyList()) { rentalRequestSheet ->
            val fragment = ManagerRentalDetailFragment(rentalRequestSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        sheetSearchBtn.setOnClickListener {
            if (selectedCategory == "리더명") filterByLeader(adapter, rentalRequestSheetList, searchSheetEdit.text.toString())
            if (selectedCategory == "작업자명") filterByWorker(adapter, rentalRequestSheetList, searchSheetEdit.text.toString())
            if (selectedCategory == "공기구명") filterByToolName(adapter, rentalRequestSheetList, searchSheetEdit.text.toString())
        }

        recyclerView.layoutManager = layoutManager
        selfRentalBtn.setOnClickListener {
            sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
            sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
            sharedViewModel.rentalRequestToolList.clear()
            val fragment = ManagerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        searchTypeSpinner = view.findViewById(R.id.SearchTypeSpinner)

        val category1Data = arrayOf("리더명", "작업자명", "공기구명")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category1Data)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchTypeSpinner.adapter = adapter1

        getRentalRequestSheetList()

        return view
    }

    fun getRentalRequestSheetList() {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX,"{toolboxId:${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                val updatedList: List<RentalRequestSheetDto> = gson.fromJson(result, type)
                rentalRequestSheetList = updatedList
                requireActivity().runOnUiThread {
                    (recyclerView.adapter as RentalRequestSheetAdapter).updateList(updatedList)
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun filterByLeader(adapter: RentalRequestSheetAdapter, sheets: List<RentalRequestSheetDto>, keyword: String) {
        val newList: MutableList<RentalRequestSheetDto> = mutableListOf()
        for (sheet in sheets) {
            if (keyword in sheet.leaderDto.name) {
                newList.add(sheet)
            }
        }
        adapter.updateList(newList)
    }
    fun filterByWorker(adapter: RentalRequestSheetAdapter, sheets: List<RentalRequestSheetDto>, keyword: String) {
        val newList: MutableList<RentalRequestSheetDto> = mutableListOf()
        for (sheet in sheets) {
            if (keyword in sheet.workerDto.name) {
                newList.add(sheet)
            }
        }
        adapter.updateList(newList)
    }
    fun filterByToolName(adapter: RentalRequestSheetAdapter, sheets: List<RentalRequestSheetDto>, keyword: String) {
        val newList: MutableList<RentalRequestSheetDto> = mutableListOf()
        for (sheet in sheets) {
            for (tool in sheet.toolList)
                if (keyword in tool.toolDto.name) {
                    newList.add(sheet)
                }
        }
        adapter.updateList(newList)
    }
}