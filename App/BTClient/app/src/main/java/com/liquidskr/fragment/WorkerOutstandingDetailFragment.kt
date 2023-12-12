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
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.tool.ToolState
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class WorkerOutstandingDetailFragment(outstandingSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingSheet.rentalSheetDto.toolList

    val outstandingSheet: OutstandingRentalSheetDto = outstandingSheet

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: ImageButton

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_return_detail, container, false)

        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)

        returnerName.text = "반납자: " + outstandingSheet.rentalSheetDto.workerDto.name
        workerName.text = "대여자: " + outstandingSheet.rentalSheetDto.workerDto.name
        leaderName.text = "리더: " + outstandingSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = "대여일시: " + LocalDateTime.parse(outstandingSheet.rentalSheetDto.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is OutstandingDetailAdapter) {
                    val outstandingRentalToolDtoList: MutableList<ReturnToolFormDto> = mutableListOf()
                    for (rentalTool: RentalToolDto in adapter.selectedToolsToReturn) {
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.outstandingRentalTools.indexOf(rentalTool)) as? OutstandingDetailAdapter.OutstandingRentalToolViewHolder
                        val count = holder?.toolCount?.text.toString().toInt()
                        outstandingRentalToolDtoList.add(ReturnToolFormDto(rentalTool.id, rentalTool.toolDto.id, count, ToolState.GOOD, ""))
                    }
                    val temp = ReturnSheetFormDto(outstandingSheet.rentalSheetDto.id, outstandingSheet.rentalSheetDto.workerDto.id,outstandingSheet.rentalSheetDto.approverDto.id, sharedViewModel.toolBoxId, outstandingRentalToolDtoList)
                    Log.d("asdf_",gson.toJson(temp))
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = OutstandingDetailAdapter(toolList)
        recyclerView.adapter = adapter

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