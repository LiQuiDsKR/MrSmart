package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.RentalRequestSheetApprove
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import java.io.IOException
import java.lang.reflect.Type

class ManagerRentalDetailFragment(rentalRequestSheet: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalRequestToolDto> = rentalRequestSheet.toolList

    val rentalRequestSheet: RentalRequestSheetDto = rentalRequestSheet

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rental_detail, container, false)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.rental_detail_confirmBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        workerName.text = "대여자: " + rentalRequestSheet.workerDto.name
        leaderName.text = "리더: " + rentalRequestSheet.leaderDto.name
        timeStamp.text = "신청일시: " + rentalRequestSheet.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val adapter = RentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalRequestToolAdapter) {
                    val rentalRequestToolDtoList: MutableList<RentalRequestToolDto> = mutableListOf()
                    for (tool: RentalRequestToolDto in adapter.selectedToolsToRental) { // ##############
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.rentalRequestTools.indexOf(tool)) as? RentalRequestToolAdapter.RentalRequestToolViewHolder
                        val toolCount = holder?.toolCount?.text?.toString()?.toIntOrNull() ?: 0
                        Log.d("cnt",holder?.toolName.toString())
                        Log.d("cnt",toolCount.toString())
                        rentalRequestToolDtoList.add(RentalRequestToolDto(rentalRequestSheet.id, rentalRequestSheet, tool.toolDto, toolCount, ""))
                    }
                    val modifiedRentalRequestSheet = RentalRequestSheetDto(rentalRequestSheet.id, rentalRequestSheet.workerDto, rentalRequestSheet.leaderDto, rentalRequestSheet.toolboxDto,rentalRequestSheet.status,rentalRequestSheet.eventTimestamp, rentalRequestToolDtoList)
                    val rentalRequestSheetApprove = RentalRequestSheetApprove(modifiedRentalRequestSheet, sharedViewModel.loginManager.id)
                    try {
                        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_APPROVE, gson.toJson(rentalRequestSheetApprove), object:
                            BluetoothManager.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                Log.d("asdf","대여 승인 완료")

                                requireActivity().supportFragmentManager.popBackStack()
                            }

                            override fun onError(e: Exception) {
                                handleBluetoothError(gson.toJson(rentalRequestSheetApprove))
                                e.printStackTrace()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        })
                    } catch (e: IOException) {

                    }
                }
            }
        }

        return view
    }
    private fun handleBluetoothError(json: String) {
        Log.d("STANDBY","STANDBY ACCESS")
        var dbHelper = DatabaseHelper(requireContext())
        dbHelper.insertStandbyData(gson.toJson(json), "RENTAL","STANDBY" )
        dbHelper.close()
    }
}