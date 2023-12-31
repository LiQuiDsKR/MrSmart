package com.liquidskr.fragment

import SharedViewModel
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
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.RentalRequestSheetApprove
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.tool.ToolDto
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

<<<<<<< HEAD
        val dbHelper = DatabaseHelper(requireContext())



        workerName.text = rentalRequestSheet.workerDto.name
        leaderName.text = rentalRequestSheet.leaderDto.name
        timeStamp.text = rentalRequestSheet.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
=======
        workerName.text = "대여자: " + rentalRequestSheet.workerDto.name
        leaderName.text = "리더: " + rentalRequestSheet.leaderDto.name
        timeStamp.text = "신청일시: " + rentalRequestSheet.eventTimestamp //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
>>>>>>> parent of b0b937f (20231229)

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
                val tag = fixCode(qrEditText.text.toString().replace("\n", ""))
                try {
                    var dbHelper = DatabaseHelper(requireContext())
                    var taggedTool = dbHelper.getToolByTag(tag).toToolDto()

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
                            if (tool.toolDto.id == taggedTool.id) {
                                var modifiedTag = ""
                                if (tool.Tags == "") {
                                    modifiedTag = tag
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(activity, "${taggedTool.name} 에 ${tag} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    adapter.tagAdded(modifiedRentalRequestTool)
                                } else {
                                    val tags = tool.Tags.toString().split(",")
                                    if (tag in tags) {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(activity, "이미 ${taggedTool.name} 에 ${tag} 를 확인했습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(activity, "${taggedTool.name} 에 ${tag} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                        modifiedTag = tool.Tags + "," + tag
                                    }
                                }
                                Log.d("a", modifiedTag)
                                modifiedRentalRequestTool = RentalRequestToolDto(tool.id, tool.toolDto, tool.count, modifiedTag)
                                Log.d("a",modifiedRentalRequestTool.toString())
                            }
                            rentalRequestToolDtoList.add(modifiedRentalRequestTool)
                            Log.d("a",rentalRequestToolDtoList.toString())
                        }
                        val modifiedRentalRequestSheet = RentalRequestSheetDto(rentalRequestSheet.id, rentalRequestSheet.workerDto, rentalRequestSheet.leaderDto, rentalRequestSheet.toolboxDto,rentalRequestSheet.status,rentalRequestSheet.eventTimestamp, rentalRequestToolDtoList)
                        Log.d("a",modifiedRentalRequestSheet.toString())
                        rentalRequestSheet = modifiedRentalRequestSheet
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(activity, "해당 QR은 대여 신청 목록에 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
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
                                requireActivity().runOnUiThread {
                                    Toast.makeText(activity, "대여 승인 완료", Toast.LENGTH_SHORT).show()
                                }
                                requireActivity().supportFragmentManager.popBackStack()
                            }

                            override fun onError(e: Exception) {
                                if (!standbyAlreadySent) {
                                    requireActivity().runOnUiThread {
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
    fun fixCode(input: String): String {
        val typoMap = mapOf(
            'ㅁ' to 'A',
            'ㅠ' to 'B',
            'ㅊ' to 'C',
            'ㅇ' to 'D',
            'ㄷ' to 'E',
            'ㄹ' to 'F',
            'ㅎ' to 'G'
        )
        val correctedText = StringBuilder()
        for (char in input) {
            val correctedChar = typoMap[char] ?: char
            correctedText.append(correctedChar)
        }
        return correctedText.toString()
    }
    private fun handleBluetoothError(json: String) {
        Log.d("STANDBY","STANDBY ACCESS")
        var dbHelper = DatabaseHelper(requireContext())
        dbHelper.insertStandbyData(gson.toJson(json), "RENTAL","STANDBY","")
        dbHelper.close()
    }
}