package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.mrsmart.standard.rental.RentalRequestSheetDto
import java.lang.reflect.Type

class ManagerRentalFragment() : Fragment() {
    lateinit var searchTypeSpinner: Spinner
    lateinit var recyclerView: RecyclerView
    lateinit var selfRentalBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager
    lateinit var rentalRequestSheetDtoList: List<RentalRequestSheetDto>

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_rental, container, false)

        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)
        val layoutManager = LinearLayoutManager(requireContext())

        rentalRequestSheetDtoList = emptyList()

        recyclerView.layoutManager = layoutManager
        selfRentalBtn.setOnClickListener {
            val fragment = ManagerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        searchTypeSpinner = view.findViewById(R.id.SearchTypeSpinner)

        val category1Data = arrayOf("리더명", "대여자명", "공기구명")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category1Data)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchTypeSpinner.adapter = adapter1

        getRentalRequestSheetList()
        Thread.sleep(1000)
        val adapter = RentalRequestSheetAdapter(rentalRequestSheetDtoList) { rentalRequestSheet ->
            val fragment = ManagerRentalDetailFragment(rentalRequestSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        return view
    }

    fun getRentalRequestSheetList() {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX,"{toolboxId:${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                Log.d("asdf",result)
                rentalRequestSheetDtoList = gson.fromJson(result, type)
                Log.d("asdf",rentalRequestSheetDtoList.toString())
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
}