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
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.RentalRequestSheetApprove
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.StandbyParam
import com.mrsmart.standard.standby.StandbySheet
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.lang.reflect.Type

class ManagerRentalDetailFragment(rentalRequestSheet: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalRequestToolDto> = rentalRequestSheet.toolList

    var rentalRequestSheet: RentalRequestSheetDto = rentalRequestSheet

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var qrEditText: EditText
    private lateinit var qrcodeBtn: LinearLayout
    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager
    private val handler = Handler(Looper.getMainLooper())

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

        qrEditText = view.findViewById((R.id.QR_EditText))
        qrcodeBtn = view.findViewById(R.id.QRcodeBtn)
        backButton = view.findViewById(R.id.backButton)

        workerName.text = rentalRequestSheet.workerDto.name
        leaderName.text = rentalRequestSheet.leaderDto.name
        timeStamp.text = rentalRequestSheet.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


        var adapter = RentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        qrcodeBtn.setOnClickListener {
            if (!qrEditText.isFocused) {
                qrEditText.requestFocus()
            }
        }
        qrEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val tag = qrEditText.text.toString().replace("\n", "")
                try {
                    lateinit var taggedTool: ToolDto
                    bluetoothManager.requestData(RequestType.TAG, "{tag:\"${tag}\"}", object:BluetoothManager.RequestCallback{ // TagDto 받기
                        override fun onSuccess(result: String, type: Type) {
                            if (result != null) {
                                val tag: TagDto = gson.fromJson(result, type)
                                taggedTool = tag.toolDto

                                var toolDtoList = listOf<ToolDto>()
                                for (rentalRequestTool:RentalRequestToolDto in rentalRequestSheet.toolList) {
                                    toolDtoList = toolDtoList.plus(rentalRequestTool.toolDto)
                                }
                                var toolIdList = listOf<Long>()
                                for (tool in toolDtoList) {
                                    toolIdList = toolIdList.plus(tool.id)
                                }
                                if (taggedTool.id in toolIdList) {
                                    val rentalRequestToolDtoList: MutableList<RentalRequestToolDto> = mutableListOf()
                                    for (tool: RentalRequestToolDto in rentalRequestSheet.toolList) {
                                        var modifiedRentalRequestTool = RentalRequestToolDto(tool.id, tool.toolDto, tool.count, tool.Tags)
                                        var modifiedTag = ""
                                        if (tool.toolDto.id == taggedTool.id) {
                                            if (tool.Tags == null || tool.Tags == "") {
                                                modifiedTag = tag.macaddress
                                                handler.post {
                                                    Toast.makeText(
                                                        activity,
                                                        "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                adapter.tagAdded(modifiedRentalRequestTool)
                                            } else {
                                                if ("," in tool.Tags) { // 여러개 있다면
                                                    val tags = tool.Tags.split(",")
                                                    if (!(tag.macaddress in tags)) {
                                                        modifiedTag = tool.Tags + "," + tag
                                                        handler.post {
                                                            Toast.makeText(
                                                                activity,
                                                                "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    } else {
                                                        modifiedTag = tag.macaddress
                                                    }
                                                } else { // 한개 있다면
                                                    if (!(tool.Tags == tag.macaddress)) {
                                                        modifiedTag = tool.Tags + "," + tag
                                                        handler.post {
                                                            Toast.makeText(activity, "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        modifiedTag = tool.Tags
                                                    }
                                                }
                                            }
                                        } else {
                                            if (tool.Tags == null) {
                                                modifiedTag = ""
                                            } else {
                                                modifiedTag = tool.Tags
                                            }
                                        }
                                        modifiedRentalRequestTool = RentalRequestToolDto(tool.id, tool.toolDto, tool.count, modifiedTag)
                                        rentalRequestToolDtoList.add(modifiedRentalRequestTool)
                                    }
                                    val modifiedRentalRequestSheet = RentalRequestSheetDto(rentalRequestSheet.id, rentalRequestSheet.workerDto, rentalRequestSheet.leaderDto, rentalRequestSheet.toolboxDto,rentalRequestSheet.status,rentalRequestSheet.eventTimestamp, rentalRequestToolDtoList)
                                    rentalRequestSheet = modifiedRentalRequestSheet
                                } else {
                                    handler.post {
                                        Toast.makeText(activity, "해당 QR은 대여 신청 목록에 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
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

                // Use a Handler to set focus after a delay
                Handler().postDelayed({
                    qrEditText.requestFocus()
                }, 100) // You can adjust the delay as needed

                return@setOnEditorActionListener true
            }
            false
        }

        confirmBtn.setOnClickListener {
            var standbyAlreadySent = false
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalRequestToolAdapter) {
                    val rentalRequestToolDtoList: MutableList<RentalRequestToolDto> = mutableListOf()
                    for (toolId in adapter.selectedToolsToRental) {
                        for (tool in rentalRequestSheet.toolList) {
                            if (tool.id == toolId) {
                                for (i in adapter.selectedToolsToRental.indices) {
                                    if (tool.id == adapter.selectedToolsToRental[i]) {
                                        val holder = recyclerView.findViewHolderForAdapterPosition(i) as? RentalRequestToolAdapter.RentalRequestToolViewHolder
                                        val toolCount = holder?.toolCount?.text?.toString()?.toIntOrNull() ?: 0

                                        Log.d("cnt",holder?.toolName.toString())
                                        Log.d("cnt",toolCount.toString())
                                        rentalRequestToolDtoList.add(RentalRequestToolDto(tool.id, tool.toolDto, toolCount, tool.Tags))
                                    }
                                }
                            }
                        }
                    }
                    val modifiedRentalRequestSheet = RentalRequestSheetDto(rentalRequestSheet.id, rentalRequestSheet.workerDto, rentalRequestSheet.leaderDto, rentalRequestSheet.toolboxDto,rentalRequestSheet.status,rentalRequestSheet.eventTimestamp, rentalRequestToolDtoList)
                    val rentalRequestSheetApprove = RentalRequestSheetApprove(modifiedRentalRequestSheet, sharedViewModel.loginManager.id)
                    try {
                        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_APPROVE, gson.toJson(rentalRequestSheetApprove), object:
                            BluetoothManager.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                if (result == "good") {
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 완료", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                } else {
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                }

                            }

                            override fun onError(e: Exception) {
                                if (!standbyAlreadySent) {
                                    handler.post {
                                        Toast.makeText(activity, "대여 승인 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    handleBluetoothError(gson.toJson(rentalRequestSheetApprove))
                                    e.printStackTrace()
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                            }
                        })
                    } catch (e: IOException) {

                    }
                }
            }
            standbyAlreadySent = true
        }

        return view
    }
    private fun handleBluetoothError(json: String) {
        Log.d("STANDBY","STANDBY ACCESS")
        var dbHelper = DatabaseHelper(requireContext())
        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val standbySheet = StandbySheet(json,timestamp)
        var final = gson.toJson(standbySheet)
        dbHelper.insertStandbyData(final, "RENTAL","STANDBY", "")
        dbHelper.close()
    }
}