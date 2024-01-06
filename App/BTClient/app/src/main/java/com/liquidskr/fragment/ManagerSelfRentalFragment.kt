package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.liquidskr.btclient.MyScannerListener
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalToolAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import com.mrsmart.standard.standby.StandbyParam
import com.mrsmart.standard.standby.RentalRequestSheetFormStandbySheet

import com.mrsmart.standard.tool.ToolWithCount
import java.lang.reflect.Type
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class ManagerSelfRentalFragment() : Fragment(), RentalToolAdapter.OnDeleteItemClickListener {
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
    private val handler = Handler(Looper.getMainLooper())

    private var active = false
    val listener: MyScannerListener.Listener = object : MyScannerListener.Listener {
        override fun onTextFinished() {
            if (!active) {
                return
            }
            val dbHelper = DatabaseHelper(requireContext())
            val adapter = recyclerView.adapter as RentalToolAdapter
            val tbt = sharedViewModel.qrScannerText
            sharedViewModel.qrScannerText = ""
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
                handler.post {
                    Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var worker: MembershipSQLite? = null
    var leader: MembershipSQLite? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_self_rental, container, false)
        var dbHelper = DatabaseHelper(requireContext())

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

        var toolList: MutableList<ToolWithCount> = mutableListOf() // fragment 이동 전 공구 목록
        toolList.addAll(sharedViewModel.toolWithCountList)
        val newToolList: MutableList<ToolWithCount> = mutableListOf() // toolFindFragment에서 추가한것 추가

        for (id in sharedViewModel.rentalRequestToolIdList) {
            var toolWithCountFound = false

            for (toolWithCount in toolList) {
                if (id == toolWithCount.tool.id) {
                    // 이미 존재하는 경우
                    toolWithCount.count += 1
                    toolWithCountFound = true
                    break
                }
            }
            if (!toolWithCountFound) {
                // 존재하지 않는 경우
                val toolWithCount = ToolWithCount(dbHelper.getToolById(id), 1)
                newToolList.add(toolWithCount)
            }
        }

        var finalToolList: MutableList<ToolWithCount> = toolList
        finalToolList.addAll(newToolList)

        val adapter = RentalToolAdapter(finalToolList, this)
        sharedViewModel.toolWithCountList = adapter.tools
        sharedViewModel.rentalRequestToolIdList.clear()

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
                    handler.post {
                        Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                qrEditText.text.clear()
                qrEditText.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }
        workerSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(1) // type = 1
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        leaderSearchBtn.setOnClickListener {
            val fragment = MembershipFindFragment.newInstance(2) // type = 2
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        addToolBtn.setOnClickListener {
            sharedViewModel.toolWithCountList = adapter.tools

            val fragment = ToolFindFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        confirmBtn.setOnClickListener {
            var standbyAlreadySent = false
            if (adapter is RentalToolAdapter) {
                if (!adapter.tools.isEmpty()) {
                    val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                    for (toolWithCount: ToolWithCount in adapter.tools) {
                        val toolCount = toolWithCount.count
                        rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(toolWithCount.tool.id, toolCount))
                    }
                    if (!(worker!!.code.equals(""))) {
                        if (!(leader!!.code.equals(""))) {
                            val rentalRequestSheetForm = RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, sharedViewModel.toolBoxId ,rentalRequestToolFormDtoList.toList())
                            bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_FORM, gson.toJson(rentalRequestSheetForm), object:
                                BluetoothManager.RequestCallback{
                                override fun onSuccess(result: String, type: Type) {
                                    if  (result == "good") {
                                        handler.post {
                                            Toast.makeText(requireActivity(), "대여 신청 완료", Toast.LENGTH_SHORT).show()
                                        }
                                        sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
                                        sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
                                        sharedViewModel.rentalRequestToolIdList.clear()
                                        toolList.clear()
                                        requireActivity().supportFragmentManager.popBackStack()
                                    } else {
                                        handler.post {
                                            Toast.makeText(activity, "대여 신청 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }

                                }
                                override fun onError(e: Exception) {
                                    if (!standbyAlreadySent) {
                                        handler.post {
                                            Toast.makeText(activity, "대여 신청 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                        }

                                        handleBluetoothError(rentalRequestSheetForm)
                                        e.printStackTrace()
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                    e.printStackTrace()
                                }
                            })

                        } else {
                            handler.post {
                                Toast.makeText(requireContext(), "리더를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        handler.post {
                            Toast.makeText(requireContext(), "작업자를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    handler.post {
                        Toast.makeText(requireContext(), "공기구를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        clearBtn.setOnClickListener {
            sharedViewModel.toolWithCountList.clear()
            var toolList: MutableList<ToolWithCount> = mutableListOf()
            adapter.updateList(toolList)
        }

        recyclerView.adapter = adapter

        qrEditText.requestFocus()
        return view
    }

    fun recyclerViewUpdate(adapter: RentalToolAdapter) {
        var dbHelper = DatabaseHelper(requireContext())
        var toolList: MutableList<ToolWithCount> = mutableListOf()
        for (id in sharedViewModel.rentalRequestToolIdList) {
            toolList.add(ToolWithCount(dbHelper.getToolById(id),1))
        }
        adapter.updateList(toolList)
        recyclerView.adapter = adapter
    }
    override fun onDestroyView() {
        active = false
        super.onDestroyView()
    }
    private fun handleBluetoothError(sheet: RentalRequestSheetFormDto) {
        Log.d("STANDBY","STANDBY ACCESS")

        val rentalRequestSheetForm = sheet
        val toolList = rentalRequestSheetForm.toolList
        var dbHelper = DatabaseHelper(requireContext())
        val names: Pair<String, String> = Pair(dbHelper.getMembershipById(rentalRequestSheetForm.workerDtoId).name ,dbHelper.getMembershipById(rentalRequestSheetForm.leaderDtoId).name)
        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var pairToolList = listOf<Pair<String,Int>>()
        for (tool in toolList) {
            val name = dbHelper.getToolById(tool.toolDtoId).name
            val count = tool.count
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }
        val detail = gson.toJson(StandbyParam(0, names.first, names.second, timestamp, pairToolList))
        val standbySheet = RentalRequestSheetFormStandbySheet(sheet, timestamp)
        dbHelper.insertStandbyData(gson.toJson(standbySheet), "RENTALREQUEST","STANDBY", detail)
        dbHelper.close()
    }
    override fun onDeleteItemClicked(list: MutableList<ToolWithCount>) {
        sharedViewModel.toolWithCountList = list
    }
}