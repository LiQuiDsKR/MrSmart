package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.liquidskr.btclient.RentalToolAdapter
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class ManagerRentalDetailFragment(rentalRequestSheet: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalRequestToolDto> = rentalRequestSheet.toolList

    val rentalRequestSheet: RentalRequestSheetDto = rentalRequestSheet

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: ImageButton

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rental_detail, container, false)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)
        recyclerView = view.findViewById(R.id.Manager_Rental_Detail_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        workerName.text = "대여자: " + rentalRequestSheet.workerDto.name
        leaderName.text = "리더: " + rentalRequestSheet.leaderDto.name
        timeStamp.text = "신청일시: " + LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val adapter = RentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalRequestToolAdapter) {
                    val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> =
                        mutableListOf()
                    for (tool: RentalRequestToolDto in adapter.selectedToolsToRental) { // ##############
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.rentalRequestTools.indexOf(tool)) as? RentalToolAdapter.RentalToolViewHolder
                        val toolCount = holder?.toolCount?.text?.toString()?.toIntOrNull() ?: 0
                        rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(tool.id, toolCount))
                        val temp = RentalRequestSheetFormDto("DefaultWorkName", rentalRequestSheet.workerDto.id, rentalRequestSheet.leaderDto.id,sharedViewModel.toolBoxId, rentalRequestToolFormDtoList)
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        return view
    }

    fun getOutstandingRentalSheetList(): List<OutstandingRentalSheetDto> {
        /*
        bluetoothManager.dataSend("REQUEST_RentalRequestSheetList")
        if (bluetoothManager.dataReceiveSingle().equals("Ready")) {
            val sendMessage =
                gson.toJson(RentalRequestSheetCall(SheetStatus.REQUEST, sharedViewModel.toolBoxId))
            bluetoothManager.dataSend(sendMessage)
        }*/

        //val rentalrequestSheetListPageString = bluetoothManager.dataReceive()
        val outstandingRentalSheetListPageString = ""
        Log.d("Debug", "JSON String: $outstandingRentalSheetListPageString")
        val pagedata: Page = gson.fromJson(outstandingRentalSheetListPageString, Page::class.java)
        val listOutstandingRentalSheetDto = object : TypeToken<List<OutstandingRentalSheetDto>>(){}.type
        Log.d("Debug", "TypeToken: $listOutstandingRentalSheetDto")
        val outstandingRentalSheetDtoList: List<OutstandingRentalSheetDto> = gson.fromJson(gson.toJson(pagedata.content), listOutstandingRentalSheetDto)
        Log.d("Debug", "OutstandingRentalSheetDto List: $outstandingRentalSheetDtoList")
        return outstandingRentalSheetDtoList
    }
}