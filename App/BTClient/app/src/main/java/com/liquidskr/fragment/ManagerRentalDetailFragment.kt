package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.PendingIntentCompat.send
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.InputProcessor
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.mrsmart.standard.rental.RentalRequestSheetApproveFormDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolApproveFormDto

class ManagerRentalDetailFragment(private var rentalRequestSheetDto: RentalRequestSheetDto) : Fragment(), InputProcessor {
    private lateinit var recyclerView: RecyclerView
    private var toolList: MutableList<RentalRequestToolApproveFormDto> = mutableListOf()

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var qrEditText: EditText
    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout

    private lateinit var popupLayout : View

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rental_detail, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.rental_detail_confirmBtn)
        cancelBtn = view.findViewById(R.id.rental_detail_cancelBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        qrEditText = view.findViewById((R.id.QR_EditText))
        backButton = view.findViewById(R.id.backButton)

        workerName.text = rentalRequestSheetDto.workerDto.name
        leaderName.text = rentalRequestSheetDto.leaderDto.name
        timeStamp.text = rentalRequestSheetDto.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        for (rentalRequestToolDto in rentalRequestSheetDto.toolList) {
            toolList.add(RentalRequestToolApproveFormDto(rentalRequestToolDto.id,rentalRequestToolDto.toolDto.id, rentalRequestToolDto.count,rentalRequestToolDto.tags?:""))
        }

        var adapter = RentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            //confirm()
            /* 이거 standby

            var standbyAlreadySent = false
            if (adapter is RentalRequestToolAdapter) {
                if (adapter.selectedToolsToRental.isNotEmpty()) {
                    var toolFormList: MutableList<RentalRequestToolApproveFormDto> = mutableListOf()
                    for (rrtwc in adapter.rentalRequestToolWithCounts) { // rrtwc = rentalRequestToolWithCount
                        val tags = rrtwc.rentalRequestTool.tags ?: ""
                        val toolForm = RentalRequestToolApproveFormDto(rrtwc.rentalRequestTool.id, rrtwc.rentalRequestTool.toolDto.id, rrtwc.count, tags)
                        toolFormList.add(toolForm)
                    }
                    toolFormList = toolFormList.filter { adapter.selectedToolsToRental.contains(it.toolDtoId) }.toMutableList()
                    val sheet = rentalRequestSheet
                    val rentalRequestSheetApproveForm = RentalRequestSheetApproveFormDto(sheet.id, sheet.workerDto.id, sheet.leaderDto.id, sharedViewModel.loginManager!!.id, sharedViewModel.toolBoxId, toolFormList)
                    //블루투스
                } else {
                }
            }
            standbyAlreadySent = true
             */
        }
        cancelBtn.setOnClickListener {
            cancel()
        }

        return view
    }

    fun cancel() {
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_CANCEL
        val data = "{rentalRequestSheetId:${rentalRequestSheetDto.id}}"
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }


    fun confirm(){
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_APPROVE
        val data = RentalRequestSheetApproveFormDto(
                rentalRequestSheetDto.id,
                rentalRequestSheetDto.workerDto.id,
                rentalRequestSheetDto.leaderDto.id,
                sharedViewModel.loginManager!!.id,
                rentalRequestSheetDto.toolboxDto.id,
                toolList
        )
    }
    private fun onTagInput(tag : String){
        val type = Constants.BluetoothMessageType.TAG
        val data = "{\"tag\":\"${tag}\"}"
        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
    }



//    private fun handleBluetoothError(sheet: RentalRequestSheetApproveFormDto) {
//        Log.d("STANDBY","STANDBY ACCESS")
//        val toolList = sheet.toolList
//        var dbHelper = DatabaseHelper.getInstance()
//        val names: Pair<String, String> = Pair(dbHelper.getMembershipById(sheet.workerDtoId).name, dbHelper.getMembershipById(sheet.leaderDtoId).name)
//        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//
//        var pairToolList = listOf<Pair<String,Int>>()
//        for (tool in toolList) {
//            val name = dbHelper.getToolById(tool.toolDtoId).name
//            val count = tool.count
//            val pair = Pair(name, count)
//            pairToolList = pairToolList.plus(pair)
//        }
//
//        val detail = gson.toJson(StandbyParam(sheet.id, names.first, names.second, timestamp, pairToolList))
//        val standbySheet = RentalRequestSheetApproveStandbySheet(sheet,timestamp)
//        var final = gson.toJson(standbySheet)
//        dbHelper.insertStandbyData(final, "RENTAL","STANDBY", detail)
//        dbHelper.close()
//    }

    override fun processInput(input: String) {
        onTagInput(input)
    }
}