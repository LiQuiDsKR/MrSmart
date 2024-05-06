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
import com.google.gson.Gson
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.WorkerOutstandingDetailAdapter
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetDto
import java.lang.NullPointerException

//TODO : 파라미터 Bundle 같은 거로 빼놓으세요
class WorkerOutstandingDetailFragment(private var outstandingRentalSheet : OutstandingRentalSheetDto) : Fragment(){
    private lateinit var recyclerView: RecyclerView

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: LinearLayout
    private lateinit var backButton: ImageButton


    val gson = Gson()

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_return_detail, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val worker = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.return_detail_confirmBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton = view.findViewById(R.id.backButton)

        returnerName.text = worker.name
        workerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingRentalSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingRentalSheet.rentalSheetDto.eventTimestamp

        val adapter = WorkerOutstandingDetailAdapter(outstandingRentalSheet.rentalSheetDto.toolList.toMutableList())
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            DialogUtils.showAlertDialog("반납 신청", "정말로 반납하시겠습니까?",
                { _,_->confirm() }, { _,_-> })
        }

        return view
    }
    private fun confirm(){
        val type = Constants.BluetoothMessageType.RETURN_SHEET_REQUEST
        val data = "{outstandingRentalSheetId:${outstandingRentalSheet.id}}"
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }
}