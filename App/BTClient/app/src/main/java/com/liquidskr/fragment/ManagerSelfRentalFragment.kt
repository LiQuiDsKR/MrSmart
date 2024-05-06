package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
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
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.liquidskr.btclient.RentalRequestToolApproveAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetApproveFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolApproveFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormSelectedDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tag.TagService
import com.mrsmart.standard.tag.ToolboxToolLabelService
import com.mrsmart.standard.tool.ToolService
import com.mrsmart.standard.tool.ToolWithCount
import com.mrsmart.standard.toolbox.ToolboxService

class ManagerSelfRentalFragment() : Fragment(), InputHandler {
    private lateinit var workerSearchBtn: LinearLayout
    private lateinit var leaderSearchBtn: LinearLayout
    private lateinit var addToolBtn: LinearLayout

    private lateinit var confirmBtn: LinearLayout
    private lateinit var clearBtn: LinearLayout
    private lateinit var backButton: ImageButton

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var worker: MembershipDto
    private lateinit var leader: MembershipDto

    var gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val tagService = TagService.getInstance()
    private val toolboxToolLabelService = ToolboxToolLabelService.getInstance()
    private val toolService = ToolService.getInstance()
    private val membershipService = MembershipService.getInstance()
    private val toolboxService = ToolboxService.getInstance()

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_self_rental, container, false)

        workerSearchBtn = view.findViewById(R.id.BorrowerSearchBtn)
        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        confirmBtn = view.findViewById(R.id.ConfirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)

        backButton = view.findViewById(R.id.backButton)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)

        var adapter = RentalRequestToolAdapter(mutableListOf())

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        parentFragmentManager.setFragmentResultListener("toolIdList", this) { key, bundle ->
            val toolIdList = bundle.getLongArray("toolIdList")
            val toolList = toolIdList?.map { RentalRequestToolFormSelectedDto(it,if (adapter.containsId(it)) adapter.getCountById(it) else 0,true) }
            adapter = RentalRequestToolAdapter(toolList?.toMutableList() ?: mutableListOf())
            recyclerView.adapter = adapter
        }
        parentFragmentManager.setFragmentResultListener("workerId", this) { key, bundle ->
            val workerId = bundle.getLong("workerId")
            worker = membershipService.getMembershipById(workerId)
            workerName.text = worker.name
        }
        parentFragmentManager.setFragmentResultListener("leaderId", this) { key, bundle ->
            val leaderId = bundle.getLong("leaderId")
            leader = membershipService.getMembershipById(leaderId)
            leaderName.text = leader.name
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        workerSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(1) // type = 1 : worker
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        leaderSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(2) // type = 2 : leader
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        addToolBtn.setOnClickListener {
            val fragment = ToolFindFragment(adapter.getResult().map{it.toolDtoId}.toMutableList())
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        clearBtn.setOnClickListener {
            //workerName.text = ""
            //leaderName.text = ""
            adapter = RentalRequestToolAdapter(mutableListOf())
            recyclerView.adapter = adapter
        }
        confirmBtn.setOnClickListener{
            if (workerName.text=="") {
                DialogUtils.showAlertDialog("작업자 미선택", "작업자가 선택되지 않았습니다. 작업자를 선택해주세요.")
                return@setOnClickListener
            }
            if (leaderName.text==""){
                DialogUtils.showAlertDialog("리더 미선택","리더가 선택되지 않았습니다. 리더를 선택해주세요.")
                return@setOnClickListener
            }

            if (adapter.isNothingSelected()){
                DialogUtils.showAlertDialog("선택된 항목 없음","선택한 공기구가 없습니다. 화면의 목록을 터치해서 공기구를 선택한 후, 승인해주세요.")
            }else if (!adapter.areAllSelected()){
                DialogUtils.showAlertDialog("대여 승인","신청된 공기구 중 일부만 선택하셨습니다. 정말로 승인하시겠습니까?",
                    { _,_->confirm() }, { _,_-> })
            }else{
                // 240506 위의 두 조건 분기는 들어갈 일 없음.
                DialogUtils.showAlertDialog("대여 승인", "정말로 승인하시겠습니까?",
                    { _,_->confirm() }, { _,_-> })
            }
        }
        recyclerView.adapter = adapter
        return view
    }

    private fun confirm(){
        val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_FORM
        val data = gson.toJson(RentalRequestSheetFormDto(
            "",
            worker.id,
            leader.id,
            toolboxService.getToolbox().id,
            (recyclerView.adapter as RentalRequestToolAdapter).getResult().map{
                RentalRequestToolFormDto(it.toolDtoId,it.count)
            }
        ))
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }

    override fun handleInput(input: String) {
        if (toolboxToolLabelService.isToolboxToolLabelExist(input)) {
            val toolId = toolService.getToolByTBT(input).id
            (recyclerView.adapter as RentalRequestToolAdapter).addTool(toolId)
        }else{
            val type = Constants.BluetoothMessageType.TAG
            val data = "{\"tag\":\"$input\"}"
            bluetoothManager?.send(type,data)
        }
    }

    override fun handleTagResponse(response: Any) {
        if (response is TagDto) (recyclerView.adapter as RentalRequestToolAdapter).addTag(response)
    }

    //toolboxToolLabel input은 handleInput에서 바로 처리됩니다 (local DB에서 쿼리)
    override fun handleToolboxToolLabelResponse(response: Any) {}

    override fun onResume() {
        super.onResume()
        tagService.inputHandler=this
        toolboxToolLabelService.inputHandler=this
    }

    override fun onDetach() {
        super.onDetach()
        tagService.inputHandler=null
        toolboxToolLabelService.inputHandler=null
    }

}