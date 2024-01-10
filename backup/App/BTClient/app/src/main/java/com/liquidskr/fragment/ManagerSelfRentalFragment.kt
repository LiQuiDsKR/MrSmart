package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalToolAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import com.mrsmart.standard.rental.StandbyParam
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.tool.ToolWithCount
import java.lang.reflect.Type
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class ManagerSelfRentalFragment() : Fragment() {
    private lateinit var workerSearchBtn: LinearLayout
    private lateinit var leaderSearchBtn: LinearLayout
    private lateinit var addToolBtn: LinearLayout
    private lateinit var confirmBtn: LinearLayout
    private lateinit var clearBtn: LinearLayout
    private lateinit var qrCodeBtn: LinearLayout
    private lateinit var qrEditText: EditText
    private lateinit var backButton: ImageButton

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager

    var worker: MembershipSQLite? = null
    var leader: MembershipSQLite? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_self_rental, container, false)
        val dbHelper = DatabaseHelper(requireContext())

        workerSearchBtn = view.findViewById(R.id.BorrowerSearchBtn)
        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        confirmBtn = view.findViewById(R.id.ConfirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)
        qrCodeBtn = view.findViewById(R.id.QRcodeBtn)
        qrEditText = view.findViewById(R.id.QREditText)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        backButton = view.findViewById(R.id.backButton)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val toolList: MutableList<ToolWithCount> = mutableListOf() // fragment 이동 전 공구 목록
        toolList.addAll(sharedViewModel.toolWithCountList)

        val newToolList: MutableList<ToolWithCount> = mutableListOf() // toolFindFragment에서 추가한것 추가
        for (id in sharedViewModel.rentalRequestToolIdList) { // 중복체크안되어잇음
            val toolWithCount = ToolWithCount(dbHelper.getToolById(id), 1)
            newToolList.add(toolWithCount)
        }

        val adapter = RentalToolAdapter(newToolList)
        //var finalToolList: MutableList<ToolWithCount> = toolList
        //finalToolList.addAll(newToolList)
        //adapter.updateList(newToolList)
        sharedViewModel.toolWithCountList = adapter.tools

        worker = sharedViewModel.worker
        workerName.text = sharedViewModel.worker.name
        leader = sharedViewModel.leader
        leaderName.text = sharedViewModel.leader.name

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        qrCodeBtn.setOnClickListener {
            if (!qrEditText.isFocused) {
                qrEditText.requestFocus()
            }
        }
        qrEditText.setOnEditorActionListener { _, actionId, event ->
            Log.d("tst","textEditted")
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val tbt = qrEditText.text.toString().replace("\n", "")

                Log.d("tst",tbt)
                try {
                    val taggedTool = dbHelper.getToolByTBT(tbt)
                    val taggedToolId = taggedTool.id
                    var toolIdList: MutableList<Long> = mutableListOf()
                    for (toolWithCnt in adapter.tools) {
                        toolIdList.add(toolWithCnt.tool.id)
                    }
                    if (!(taggedToolId in toolIdList)) {
                        adapter.tools.add(ToolWithCount(taggedTool,1))
                    } else {
                        for (toolWithCnt in adapter.tools) {
                            Log.d("wrf",toolWithCnt.tool.name)
                            if (toolWithCnt.tool.id == taggedToolId) {
                                toolWithCnt.count += 1
                            }
                        }
                    }
                    sharedViewModel.toolWithCountList = adapter.tools
                    recyclerView.adapter = adapter

                } catch (e: UninitializedPropertyAccessException) {
                    Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                qrEditText.text.clear()

                // Use a Handler to set focus after a delay
                Handler().postDelayed({
                    qrEditText.requestFocus()
                }, 100) // You can adjust the delay as needed

                return@setOnEditorActionListener true
            }
            false
        }

        workerSearchBtn.setOnClickListener {
            sharedViewModel.toolWithCountList = adapter.tools

            val fragment = MembershipFindFragment.newInstance(1) // type = 1
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        leaderSearchBtn.setOnClickListener {
            sharedViewModel.toolWithCountList = adapter.tools

            val fragment = MembershipFindFragment.newInstance(2) // type = 2
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        addToolBtn.setOnClickListener {

            printFragmentStack()
            sharedViewModel.toolWithCountList = adapter.tools

            val fragment = ToolFindFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        confirmBtn.setOnClickListener {
            // var standbyAlreadySent = false
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalToolAdapter) {
                    if (!adapter.tools.isEmpty()) {
                        val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                        for (toolwithCnt in adapter.tools) {
                            rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(toolwithCnt.tool.id, toolwithCnt.count))
                        }
                        if (!(worker!!.code.equals(""))) {
                            if (!(leader!!.code.equals(""))) {
                                val rentalRequestSheet = gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, sharedViewModel.toolBoxId ,rentalRequestToolFormDtoList.toList()))
                                bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_FORM, rentalRequestSheet, object:
                                    BluetoothManager.RequestCallback{
                                    override fun onSuccess(result: String, type: Type) {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(requireContext(), "대여 신청 완료", Toast.LENGTH_SHORT).show()
                                        }
                                        sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
                                        sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
                                        sharedViewModel.rentalRequestToolIdList.clear()
                                        sharedViewModel.toolWithCountList.clear()
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                    override fun onError(e: Exception) {
                                        e.printStackTrace()
                                        /*
                                        if (!standbyAlreadySent) {
                                            requireActivity().runOnUiThread {
                                                Toast.makeText(activity, "대여 승인 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                            handleBluetoothError(gson.toJson(rentalRequestSheet))
                                            e.printStackTrace()
                                            requireActivity().supportFragmentManager.popBackStack()
                                        }*/
                                    }
                                })

                            } else {
                                Toast.makeText(requireContext(), "리더를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "작업자를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        clearBtn.setOnClickListener {
            sharedViewModel.rentalRequestToolIdList.clear()
            sharedViewModel.toolWithCountList.clear()
            sharedViewModel.toolWithCountList = adapter.tools
            var toolList: MutableList<ToolWithCount> = mutableListOf()
            adapter.updateList(toolList)
        }

        recyclerView.adapter = adapter
        return view
    }
    fun printFragmentStack() {
        val activity = requireActivity()
        val fragmentManager = activity.supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackCount) {
            val fragmentName = fragmentManager.getBackStackEntryAt(i).javaClass.simpleName
            Log.d("FragmentStack", "position : ${fragmentName}")
        }
    }
    /*
    fun recyclerViewUpdate(adapter: RentalToolAdapter) {
        var dbHelper = DatabaseHelper(requireContext())
        var toolList: MutableList<ToolWithCount> = mutableListOf()
        for (id in sharedViewModel.rentalRequestToolIdList) {
            toolList = adapter.tools
        }
        adapter.updateList(toolList)
        recyclerView.adapter = adapter
    }

    private fun handleBluetoothError(json: String) {
        Log.d("STANDBY","STANDBY ACCESS")

        val rentalRequestSheetForm = gson.fromJson(json, RentalRequestSheetFormDto::class.java)
        val toolList = rentalRequestSheetForm.toolList
        var dbHelper = DatabaseHelper(requireContext())
        val names: Pair<String, String> = Pair(dbHelper.getMembershipById(rentalRequestSheetForm.workerDtoId).name ,dbHelper.getMembershipById(rentalRequestSheetForm.leaderDtoId).name)
        // 출력 형식 지정 (선택 사항)
        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


        var pairToolList = listOf<Pair<String,Int>>()
        for (tool in toolList) {
            val name = dbHelper.getToolById(tool.toolDtoId).name
            val count = tool.count
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }
        val detail = gson.toJson(StandbyParam(0, names.first, names.second, timestamp, pairToolList))

        dbHelper.insertStandbyData(gson.toJson(json), "RENTALREQUEST","STANDBY", detail)
        dbHelper.close()
    }*/
}