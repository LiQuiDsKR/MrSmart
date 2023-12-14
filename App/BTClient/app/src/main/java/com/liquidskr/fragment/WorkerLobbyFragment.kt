package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import java.lang.reflect.Type

class WorkerLobbyFragment(worker: Membership) : Fragment() {
    lateinit var connectBtn: ImageButton
    lateinit var rentalBtn: ImageButton

    val gson = Gson()
    private lateinit var recyclerView: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_lobby, container, false)

        rentalBtn = view.findViewById(R.id.LobbyRentalBtn)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rentalBtn.setOnClickListener {
            val lobbyActivity = activity as? LobbyActivity
            val workerRentalFragment = lobbyActivity?.workerRentalFragment

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            workerRentalFragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .addToBackStack(null)
                    .commit()
            }
        }

        val adapter = OutstandingRentalSheetAdapter(getOutstandingRentalSheetList()) { outstandingRentalSheet ->
            val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        return view
    }
    fun getOutstandingRentalSheetList(): List<OutstandingRentalSheetDto> {
        var OutstandingRentalSheetDtoList = listOf<OutstandingRentalSheetDto>()
        val bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP,object: // page, size, membershipid, startDate, endDate
            BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                val pagedata: Page = gson.fromJson(result, Page::class.java)
                val listOutstandingRentalSheetDto = object : TypeToken<List<OutstandingRentalSheetDto>>(){}.type
                OutstandingRentalSheetDtoList = gson.fromJson(gson.toJson(pagedata.content), listOutstandingRentalSheetDto)
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
        return OutstandingRentalSheetDtoList
    }
}