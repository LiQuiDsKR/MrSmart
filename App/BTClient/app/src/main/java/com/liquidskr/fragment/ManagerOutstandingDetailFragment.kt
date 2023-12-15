package com.liquidskr.fragment

import SharedViewModel
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
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.tool.ToolState
import java.lang.reflect.Type

class ManagerOutstandingDetailFragment(outstandingSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingSheet.rentalSheetDto.toolList

    val outstandingSheet: OutstandingRentalSheetDto = outstandingSheet

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_return_detail, container, false)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)

        returnerName.text = "반납자: " + outstandingSheet.rentalSheetDto.workerDto.name
        workerName.text = "대여자: " + outstandingSheet.rentalSheetDto.workerDto.name
        leaderName.text = "리더: " + outstandingSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = "대여일시: " + outstandingSheet.rentalSheetDto.eventTimestamp

        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is OutstandingDetailAdapter) {
                    val outstandingRentalToolDtoList: MutableList<ReturnToolFormDto> = mutableListOf()
                    for (rentalTool: RentalToolDto in adapter.selectedToolsToReturn) {
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.outstandingRentalTools.indexOf(rentalTool)) as? OutstandingDetailAdapter.OutstandingRentalToolViewHolder
                        val count = holder?.toolCount?.text.toString().toInt()
                        outstandingRentalToolDtoList.add(ReturnToolFormDto(rentalTool.id, rentalTool.toolDto.id, count, ToolState.GOOD, ""))
                    }
                    val returnSheetForm = ReturnSheetFormDto(outstandingSheet.rentalSheetDto.id, outstandingSheet.rentalSheetDto.workerDto.id, outstandingSheet.rentalSheetDto.approverDto.id, sharedViewModel.toolBoxId, outstandingRentalToolDtoList)
                    bluetoothManager.requestData(RequestType.RETURN_SHEET_FORM, gson.toJson(returnSheetForm), object:
                        BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
                            Toast.makeText(requireContext(), "반납 승인 완료", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    //requireActivity().supportFragmentManager.popBackStack()
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