package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import java.lang.reflect.Type

class ManagerReturnFragment() : Fragment() {
    lateinit var searchTypeSpinner: Spinner
    private lateinit var recyclerView: RecyclerView

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        searchTypeSpinner = view.findViewById(R.id.SearchTypeSpinner)

        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val category1Data = arrayOf("리더명", "대여자명", "공기구명")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category1Data)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchTypeSpinner.adapter = adapter1

        //val adapter = OutstandingRentalSheetAdapter(sharedViewModel.outstandingRentalSheetList)
        val adapter = OutstandingRentalSheetAdapter(getOutstandingRentalSheetList()) { outstandingRentalSheet ->
            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        return view
    }

    fun getOutstandingRentalSheetList(): List<OutstandingRentalSheetDto> {
        var OutstandingRentalSheetDtoList = listOf<OutstandingRentalSheetDto>()
        val bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX,"",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                OutstandingRentalSheetDtoList = gson.fromJson(result, type)
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
        return OutstandingRentalSheetDtoList
    }
}