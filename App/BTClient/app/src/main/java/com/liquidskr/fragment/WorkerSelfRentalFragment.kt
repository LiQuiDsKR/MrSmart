package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils.replace
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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalToolAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormDto
import com.mrsmart.standard.tool.ToolWithCount
import java.lang.reflect.Type

class WorkerSelfRentalFragment() : Fragment(), RentalToolAdapter.OnDeleteItemClickListener {
    lateinit var leaderSearchBtn: LinearLayout
    lateinit var qrEditText: EditText
    lateinit var addToolBtn: LinearLayout
    lateinit var confirmBtn: LinearLayout
    lateinit var clearBtn: LinearLayout
    lateinit var backButton: ImageButton

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressText: TextView
    private var isPopupVisible = false // UI블로킹 end


    lateinit var workerName: TextView
    lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManagerOld: BluetoothManager_Old

    var worker: MembershipDto? = null
    var leader: MembershipDto? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_self_rental, container, false)
        val dbHelper = DatabaseHelper.getInstance()

        bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()

        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        qrEditText = view.findViewById((R.id.QR_EditText))
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        confirmBtn = view.findViewById(R.id.confirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)
        backButton = view.findViewById(R.id.backButton)

        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val toolList: MutableList<ToolWithCount> = mutableListOf() // fragment 이동 전 공구 목록
        //toolList.addAll(sharedViewModel.toolWithCountList)
        val newToolList: MutableList<ToolWithCount> = mutableListOf() // toolFindFragment에서 추가한것 추가

//        for (id in sharedViewModel.rentalRequestToolIdList) {
//            var toolWithCountFound = false
//
//            for (toolWithCount in toolList) {
//                if (id == toolWithCount.tool.id) {
//                    // 이미 존재하는 경우
//                    toolWithCount.count += 1
//                    toolWithCountFound = true
//                    break
//                }
//            }
//            if (!toolWithCountFound) {
//                // 존재하지 않는 경우
//                val toolWithCount = ToolWithCount(dbHelper.getToolById(id), 1)
//                newToolList.add(toolWithCount)
//            }
//        }
        
        val adapter = RentalToolAdapter(toolList, this)
        var finalToolList: MutableList<ToolWithCount> = toolList
        finalToolList.addAll(newToolList)
        adapter.updateList(finalToolList)
//        sharedViewModel.toolWithCountList = adapter.tools
//        sharedViewModel.rentalRequestToolIdList.clear()
//
//        worker = sharedViewModel.loginWorker
//        workerName.text = sharedViewModel.loginWorker!!.name
//        leader = sharedViewModel.leader
//        leaderName.text = sharedViewModel.leader!!.name
//
//        backButton.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//        leaderSearchBtn.setOnClickListener {
//            sharedViewModel.toolWithCountList = adapter.tools
//
//            val fragment = WorkerMembershipFindFragment.newInstance(2) // type = 2
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .addToBackStack(null)
//                .commit()
//        }
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
                    //sharedViewModel.toolWithCountList = adapter.tools
                    recyclerView.adapter = adapter

                } catch (e: UninitializedPropertyAccessException) {
                    Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                qrEditText.text.clear()
                qrEditText.requestFocus()

                return@setOnEditorActionListener true
            }
            false
        }
        qrEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                qrEditText.requestFocus()
            }
        }

        addToolBtn.setOnClickListener {
            //sharedViewModel.toolWithCountList = adapter.tools

//            //val fragment = ToolFindFragment()
//            //requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .addToBackStack(null)
//                .commit()
        }
        confirmBtn.setOnClickListener {
            if (adapter is RentalToolAdapter) {
                if (adapter.tools.isNotEmpty()) {
                    showPopup() // UI 블로킹
                    val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                    for (toolwithCnt in adapter.tools) {
                        rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(toolwithCnt.tool.id, toolwithCnt.count))
                    }
                    if (!(worker!!.code.equals(""))) {
                        if (!(leader!!.code.equals(""))) {
                            val rentalRequestSheet = gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, sharedViewModel.toolBoxId ,rentalRequestToolFormDtoList.toList()))
                            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_FORM, rentalRequestSheet, object:
                                BluetoothManager_Old.RequestCallback{
                                override fun onSuccess(result: String, type: Type) {
                                    handler.post {
                                        Toast.makeText(requireActivity(), "대여 신청 완료", Toast.LENGTH_SHORT).show()
                                    }
//                                    sharedViewModel.worker = null
//                                    sharedViewModel.leader = null
//                                    sharedViewModel.rentalRequestToolIdList.clear()
//                                    sharedViewModel.toolWithCountList.clear()
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                                override fun onError(e: Exception) {
                                    e.printStackTrace()
                                }
                            })

                        } else {
                            hidePopup() // UI 블로킹
                            Toast.makeText(requireContext(), "리더를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        hidePopup() // UI 블로킹
                        Toast.makeText(requireContext(), "작업자를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hidePopup() // UI 블로킹
                    Toast.makeText(requireContext(), "공기구를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
        clearBtn.setOnClickListener {
//            sharedViewModel.rentalRequestToolIdList.clear()
//            sharedViewModel.toolWithCountList.clear()
//            sharedViewModel.toolWithCountList = adapter.tools
            var toolList: MutableList<ToolWithCount> = mutableListOf()
            adapter.updateList(toolList)
        }
        qrEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                qrEditText.requestFocus()
            }
        }

        qrEditText.requestFocus()
        recyclerView.adapter = adapter
        return view
    }

    override fun onDeleteItemClicked(list: MutableList<ToolWithCount>) {
       // sharedViewModel.toolWithCountList = list
    }
    private fun showPopup() { // UI 블로킹 end
        isPopupVisible = true
        popupLayout.requestFocus()
        popupLayout.setOnClickListener {

        }
        popupLayout.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                return@setOnKeyListener true
            }
            false
        }
        popupLayout.visibility = View.VISIBLE
    }
    private fun hidePopup() {
        handler.post {
            isPopupVisible = false
            popupLayout.visibility = View.GONE
        }
    } // UI 블로킹 end
}