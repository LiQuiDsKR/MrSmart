package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolApproveAdapter
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetApproveFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolApproveFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolApproveFormSelectedDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tag.TagService
import java.lang.NullPointerException

//TODO : 파라미터 Bundle같은 걸로 빼놓으세요
class ManagerRentalDetailFragment(private var rentalRequestSheetDto: RentalRequestSheetDto) : Fragment(), InputHandler {
    private lateinit var recyclerView: RecyclerView

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout

    private val gson = Gson()
    private val loggedInMembership = MembershipService.getInstance().loggedInMembership

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rental_detail, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val manager = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

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
        timeStamp.text = rentalRequestSheetDto.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var adapter = RentalRequestToolApproveAdapter(
            rentalRequestSheetDto.toolList.map{
                RentalRequestToolApproveFormSelectedDto(
                    id = it.id,
                    toolDtoId = it.toolDto.id,
                    count = it.count,
                    tags = it.tags?:"",
                    isSelected = false
                )
            }.toMutableList()
        )
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            if (adapter.isNothingSelected()){
                DialogUtils.showAlertDialog("선택된 항목 없음","선택한 공기구가 없습니다. 화면의 목록을 터치해서 공기구를 선택한 후, 승인해주세요.")
            }else if (!adapter.areAllSelected()){
                DialogUtils.showAlertDialog("대여 승인","신청된 공기구 중 일부만 선택하셨습니다. 정말로 승인하시겠습니까?",
                    { _,_->confirm() }, { _,_-> })
            }else{
                DialogUtils.showAlertDialog("대여 승인", "정말로 승인하시겠습니까?",
                    { _,_->confirm() }, { _,_-> })
            }
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
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_APPROVE
        val data = gson.toJson(RentalRequestSheetApproveFormDto(
                rentalRequestSheetDto.id,
                rentalRequestSheetDto.workerDto.id,
                rentalRequestSheetDto.leaderDto.id,
                loggedInMembership!!.id,
                rentalRequestSheetDto.toolboxDto.id,
                (recyclerView.adapter as RentalRequestToolApproveAdapter).getResult().map{
                    RentalRequestToolApproveFormDto(it.id,it.toolDtoId,it.count,it.tags)
                }
        ))
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
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

    override fun handleInput(input: String) {
        val type = Constants.BluetoothMessageType.TAG
        val data = "{\"tag\":\"${input}\"}"
        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
    }

    override fun handleTagResponse(response: Any) {
        if (response is TagDto)
                (recyclerView.adapter as RentalRequestToolApproveAdapter).addTag(response)
    }

    override fun handleToolboxToolLabelResponse(response: Any) {}

    override fun onResume() {
        super.onResume()
        TagService.getInstance().inputHandler=this
    }

    override fun onDetach() {
        super.onDetach()
        TagService.getInstance().inputHandler=null
    }
}