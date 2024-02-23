package com.liquidskr.fragment

import SharedViewModel
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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.mrsmart.standard.rental.RentalRequestSheetApproveFormDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolApproveFormDto
import com.mrsmart.standard.standby.RentalRequestSheetApproveStandbySheet
import com.mrsmart.standard.standby.StandbyParam
import com.mrsmart.standard.tool.RentalRequestToolWithCount
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.lang.reflect.Type

class ManagerRentalDetailFragment(private var rentalRequestSheet: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: MutableList<RentalRequestToolWithCount> = mutableListOf()

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressText: TextView
    private var isPopupVisible = false // UI블로킹 end

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var qrEditText: EditText
    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout
    private lateinit var bluetoothManagerOld: BluetoothManager_Old

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rental_detail, container, false)

        bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.rental_detail_confirmBtn)
        cancelBtn = view.findViewById(R.id.rental_detail_cancelBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        qrEditText = view.findViewById((R.id.QR_EditText))
        backButton = view.findViewById(R.id.backButton)

        workerName.text = rentalRequestSheet.workerDto.name
        leaderName.text = rentalRequestSheet.leaderDto.name
        timeStamp.text = rentalRequestSheet.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        for (rentalRequestTool in rentalRequestSheet.toolList) {
            toolList.add(RentalRequestToolWithCount(rentalRequestTool, rentalRequestTool.count))
        }

        var adapter = RentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        qrEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val tag = qrEditText.text.toString().replace("\n", "")
                try {
                    lateinit var taggedTool: ToolDto
                    bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TAG, "{tag:\"${tag}\"}", object:BluetoothManager_Old.RequestCallback{ // TagDto 받기
                        override fun onSuccess(result: String, type: Type) {
                            if (result != null) {
                                val tag: TagDto = gson.fromJson(result, type)
                                taggedTool = tag.toolDto
                                for (rrtwc in adapter.rentalRequestToolWithCounts) { // rrtwc means, RentalRequestToolWithCount
                                    if (rrtwc.rentalRequestTool.toolDto.id == taggedTool.id) {
                                        rrtwc.rentalRequestTool.Tags = tag.macaddress
                                        handler.post {
                                            adapter.updateList(adapter.rentalRequestToolWithCounts)
                                            adapter.tagAdded(taggedTool.id)
                                            Toast.makeText(requireContext(), "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                        override fun onError(e: Exception) {

                        }
                    })
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

        confirmBtn.setOnClickListener {
            confirmBtn.isFocusable = false
            confirmBtn.isClickable = false

            var standbyAlreadySent = false
            if (adapter is RentalRequestToolAdapter) {
                if (adapter.selectedToolsToRental.isNotEmpty()) {
                    showPopup() // UI 블로킹
                    var toolFormList: MutableList<RentalRequestToolApproveFormDto> = mutableListOf()
                    for (rrtwc in adapter.rentalRequestToolWithCounts) { // rrtwc = rentalRequestToolWithCount
                        val tags = rrtwc.rentalRequestTool.Tags ?: ""
                        val toolForm = RentalRequestToolApproveFormDto(rrtwc.rentalRequestTool.id, rrtwc.rentalRequestTool.toolDto.id, rrtwc.count, tags)
                        toolFormList.add(toolForm)
                    }
                    toolFormList = toolFormList.filter { adapter.selectedToolsToRental.contains(it.toolDtoId) }.toMutableList()
                    val sheet = rentalRequestSheet
                    val rentalRequestSheetApproveForm = RentalRequestSheetApproveFormDto(sheet.id, sheet.workerDto.id, sheet.leaderDto.id, sharedViewModel.loginManager.id, sharedViewModel.toolBoxId, toolFormList)

                    try {
                        bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_APPROVE, gson.toJson(rentalRequestSheetApproveForm), object:
                            BluetoothManager_Old.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                if (result == "good") {
                                    hidePopup() // UI 블로킹
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 완료", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                } else {
                                    hidePopup() // UI 블로킹
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                }

                            }

                            override fun onError(e: Exception) {
                                hidePopup() // UI 블로킹
                                if (!standbyAlreadySent) {
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    handleBluetoothError(rentalRequestSheetApproveForm)
                                    e.printStackTrace()
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                            }
                        })
                    } catch (e: IOException) {

                    }
                } else {
                    handler.post {
                        Toast.makeText(requireContext(), "공기구를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            standbyAlreadySent = true
        }
        cancelBtn.setOnClickListener {
            sheetCancel()
        }

        qrEditText.requestFocus()
        return view
    }

    fun sheetCancel() {
        try {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_CANCEL, "{rentalRequestSheetId:${rentalRequestSheet.id}}", object:
                BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        handler.post {
                            Toast.makeText(activity, "대여 삭제 완료", Toast.LENGTH_SHORT).show()
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        handler.post {
                            Toast.makeText(activity, "대여 삭제 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                }

                override fun onError(e: Exception) {
                    handler.post {
                        Toast.makeText(activity, "대여 삭제 실패. 재연결 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
        } catch(e:Exception) {
            e.printStackTrace()
        }
    }
    private fun handleBluetoothError(sheet: RentalRequestSheetApproveFormDto) {
        Log.d("STANDBY","STANDBY ACCESS")
        val toolList = sheet.toolList
        var dbHelper = DatabaseHelper.getInstance()
        val names: Pair<String, String> = Pair(dbHelper.getMembershipById(sheet.workerDtoId).name, dbHelper.getMembershipById(sheet.leaderDtoId).name)
        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var pairToolList = listOf<Pair<String,Int>>()
        for (tool in toolList) {
            val name = dbHelper.getToolById(tool.toolDtoId).name
            val count = tool.count
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }

        val detail = gson.toJson(StandbyParam(sheet.id, names.first, names.second, timestamp, pairToolList))
        val standbySheet = RentalRequestSheetApproveStandbySheet(sheet,timestamp)
        var final = gson.toJson(standbySheet)
        dbHelper.insertStandbyData(final, "RENTAL","STANDBY", detail)
        dbHelper.close()
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