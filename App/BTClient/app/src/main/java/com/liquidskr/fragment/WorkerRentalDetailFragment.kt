package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.WorkerRentalRequestToolAdapter
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto

//TODO : 파라미터 Bundle같은 걸로 빼놓으세요
class WorkerRentalDetailFragment(private var rentalRequestSheetDto: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental_detail, container, false)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.rental_detail_confirmBtn)
        cancelBtn = view.findViewById(R.id.rental_detail_cancelBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton = view.findViewById(R.id.backButton)

        workerName.text = rentalRequestSheetDto.workerDto.name
        leaderName.text = rentalRequestSheetDto.leaderDto.name
        timeStamp.text = rentalRequestSheetDto.eventTimestamp
        //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var adapter = WorkerRentalRequestToolAdapter(rentalRequestSheetDto.toolList.toMutableList())
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            DialogUtils.showAlertDialog("대여 신청", "정말로 신청하시겠습니까?",
                { _,_->confirm() }, { _,_-> })
        }
        cancelBtn.setOnClickListener {
            DialogUtils.showAlertDialog("대여 목록 삭제","현재 페이지의 대여 신청 목록이 삭제됩니다.\n정말로 삭제하시겠습니까?",
                { _, _ -> cancel() }, { _,_-> })
        }
        return view
    }

    private fun cancel() {
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_CANCEL
        val data = "{rentalRequestSheetId:${rentalRequestSheetDto.id}}"
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }

    private fun confirm(){
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_APPLY
        val data = "{rentalRequestSheetId:${rentalRequestSheetDto.id}}"
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }
}