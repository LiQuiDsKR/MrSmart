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
import com.liquidskr.btclient.CustomModal
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.MyScannerListener
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.standby.StandbyParam
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.standby.ReturnSheetFormStandbySheet
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolState
import com.mrsmart.standard.tool.ToolStateParam
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ManagerOutstandingDetailFragment(private var outstandingRentalSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingRentalSheet.rentalSheetDto.toolList

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    lateinit var qrEditText: EditText
    lateinit var qrcodeBtn: LinearLayout
    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager
    private val handler = Handler(Looper.getMainLooper())

    val gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_return_detail, container, false)

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)

        qrEditText = view.findViewById((R.id.QR_EditText))
        qrcodeBtn = view.findViewById(R.id.QRcodeBtn)
        backButton = view.findViewById(R.id.backButton)

        returnerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        workerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingRentalSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingRentalSheet.rentalSheetDto.eventTimestamp

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        var newOutstandingRentalSheet = outstandingRentalSheet

        var existToolList: MutableList<RentalToolDto> = mutableListOf() // 0인 항목 미표시
        for (rentalTool in toolList) {
            if (rentalTool.outstandingCount > 0) {
                existToolList.add(rentalTool)
            }
        }
        var existToolCountList: MutableList<Pair<Long, Int>> = mutableListOf()
        for (existTool in existToolList) {
            existToolCountList.add(Pair(existTool.toolDto.id, existTool.outstandingCount))
        }
        newOutstandingRentalSheet.rentalSheetDto.toolList = existToolList


        var toolList = newOutstandingRentalSheet.rentalSheetDto.toolList
        var finalToolStateList: MutableList<Pair<Long,MutableList<ToolStateParam>>> = mutableListOf()
        for (tool in toolList) {
            finalToolStateList.add(Pair(tool.toolDto.id, mutableListOf(ToolStateParam(tool.id, ToolState.GOOD, tool.outstandingCount))))
        }

        val adapter = OutstandingDetailAdapter(recyclerView, toolList, onSetToolStateClick = { rentalTool ->
            var cnt = 0
            for (existToolCount in existToolCountList) {
                if (existToolCount.first == rentalTool.toolDto.id) {
                    cnt = existToolCount.second
                }
            }
            val customModal = CustomModal(requireContext(), cnt)
            customModal.setOnCountsConfirmedListener(object : CustomModal.OnCountsConfirmedListener {
                override fun onCountsConfirmed(counts: IntArray) {
                    for (tool in toolList) {
                        if (tool.id == rentalTool.id) {
                            finalToolStateList = finalToolStateList.filter { it.first != tool.toolDto.id }.toMutableList() // 이미 toolState를 등록한 ToolId라면 해당 항목 제거

                            var toolStateParamList: MutableList<ToolStateParam> = mutableListOf()
                            if (counts[0] > 0) toolStateParamList.add(ToolStateParam(tool.toolDto.id, ToolState.GOOD, counts[0]))
                            if (counts[1] > 0) toolStateParamList.add(ToolStateParam(tool.toolDto.id, ToolState.FAULT, counts[1]))
                            if (counts[2] > 0) toolStateParamList.add(ToolStateParam(tool.toolDto.id, ToolState.DAMAGE, counts[2]))
                            if (counts[3] > 0) toolStateParamList.add(ToolStateParam(tool.toolDto.id, ToolState.LOSS, counts[3]))
                            if (counts[4] > 0) toolStateParamList.add(ToolStateParam(tool.toolDto.id, ToolState.DISCARD, counts[4]))

                            val pair = Pair(tool.toolDto.id, toolStateParamList)
                            finalToolStateList.add(pair)
                        }
                    }
                }
            })
            customModal.show()
        }, onToolCountClick = { newToolWithCount ->
            for (index in existToolCountList.indices) {
                val pair = existToolCountList[index]
                if (pair.first == newToolWithCount.tool.id) {
                    existToolCountList[index] = Pair(pair.first, newToolWithCount.count)
                    break  // 수정이 완료되었으므로 반복 종료
                }
            }
            newOutstandingRentalSheet.rentalSheetDto.toolList = existToolList
        })

        recyclerView.adapter = adapter

        qrcodeBtn.setOnClickListener {
            if (!qrEditText.isFocused) {
                qrEditText.requestFocus()
            }
        }

        qrEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val tag = fixCode(qrEditText.text.toString().replace("\n", ""))
                try {
                    lateinit var taggedTool: ToolDto
                    bluetoothManager.requestData(RequestType.TAG, "{tag:\"${tag}\"}", object:BluetoothManager.RequestCallback{ // TagDto 받기
                        override fun onSuccess(result: String, type: Type) {
                            if (result != "null") {
                                val tag: TagDto = gson.fromJson(result, type)
                                taggedTool = tag.toolDto

                                var toolDtoList = listOf<ToolDto>()
                                for (rentalToolDto: RentalToolDto in newOutstandingRentalSheet.rentalSheetDto.toolList) {
                                    toolDtoList = toolDtoList.plus(rentalToolDto.toolDto)
                                }
                                var toolIdList = listOf<Long>()
                                for (tool in toolDtoList) {
                                    toolIdList = toolIdList.plus(tool.id)
                                }
                                if (taggedTool.id in toolIdList) {
                                    val rentalToolList: MutableList<RentalToolDto> = mutableListOf()
                                    for (tool: RentalToolDto in newOutstandingRentalSheet.rentalSheetDto.toolList) {
                                        var modifiedRentalTool = RentalToolDto(tool.id, tool.toolDto, tool.count, tool.outstandingCount, tool.Tags?:"null")
                                        var modifiedTag = ""
                                        if (tool.toolDto.id == taggedTool.id) {
                                            if (tool.Tags == null || tool.Tags == "") {
                                                modifiedTag = tag.macaddress
                                                handler.post {
                                                    Toast.makeText(activity, "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                                    adapter.tagAdded(modifiedRentalTool)
                                                }

                                            } else {
                                                if ("," in tool.Tags) { // 여러개 있다면
                                                    val tags = tool.Tags.split(",")
                                                    if (!(tag.macaddress in tags)) {
                                                        modifiedTag = tool.Tags + "," + tag
                                                        handler.post {
                                                            Toast.makeText(activity, "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } else {
                                                        modifiedTag = tag.macaddress
                                                    }
                                                } else { // 한개 있다면

                                                    if (!(tool.Tags == tag.tagGroup)) {
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
                                        modifiedRentalTool = RentalToolDto(tool.id, tool.toolDto, tool.count, tool.outstandingCount, modifiedTag)
                                        rentalToolList.add(modifiedRentalTool)
                                    }
                                    val rentalSheet = RentalSheetDto(outstandingRentalSheet.rentalSheetDto.id, outstandingRentalSheet.rentalSheetDto.workerDto, outstandingRentalSheet.rentalSheetDto.leaderDto, outstandingRentalSheet.rentalSheetDto.approverDto, outstandingRentalSheet.rentalSheetDto.toolboxDto, outstandingRentalSheet.rentalSheetDto.eventTimestamp, rentalToolList)
                                    val outStandingRentalSheetDto = OutstandingRentalSheetDto(outstandingRentalSheet.id, rentalSheet, outstandingRentalSheet.totalCount, outstandingRentalSheet.totalOutstandingCount,outstandingRentalSheet.outstandingState)
                                    Log.d("newRentalSheet",outStandingRentalSheetDto.toString())
                                    outstandingRentalSheet = outStandingRentalSheetDto
                                } else {
                                    handler.post {
                                        Toast.makeText(activity, "해당 QR은 대여 신청 목록에 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                handler.post {
                                    Toast.makeText(activity, "서버에서 해당 태그를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun onError(e: Exception) {

                        }
                    })
                } catch (e: UninitializedPropertyAccessException) {
                    handler.post {
                        Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
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

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            confirmBtn.isFocusable = false
            confirmBtn.isClickable = false

            var standbyAlreadySent = false
            if (adapter is OutstandingDetailAdapter) {
                val returnToolFormDtoList: MutableList<ReturnToolFormDto> = mutableListOf()
                val rentalToolList: MutableList<RentalToolDto> = mutableListOf()
                for (toolId in adapter.selectedToolsToReturn) {
                    for (tool in newOutstandingRentalSheet.rentalSheetDto.toolList) {
                        if (tool.id == toolId) {
                            rentalToolList.add(tool)
                        }
                    }
                }
                for (rentalTool in rentalToolList) {
                    for (finalToolState in finalToolStateList) {
                        if (rentalTool.toolDto.id == finalToolState.first) {
                            for (tool in finalToolState.second) {
                                val tags = rentalTool.Tags ?: ""
                                returnToolFormDtoList.add(ReturnToolFormDto(rentalTool.id, rentalTool.toolDto.id, tool.count, tool.state, tags))
                            }
                        }
                    }
                }
                val returnSheetForm = ReturnSheetFormDto(newOutstandingRentalSheet.rentalSheetDto.id, newOutstandingRentalSheet.rentalSheetDto.workerDto.id, newOutstandingRentalSheet.rentalSheetDto.approverDto.id, sharedViewModel.toolBoxId, returnToolFormDtoList)
                bluetoothManager.requestData(RequestType.RETURN_SHEET_FORM, gson.toJson(returnSheetForm), object:
                    BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        if (result == "good") {
                            handler.post {
                                Toast.makeText(activity, "반납 승인 완료", Toast.LENGTH_SHORT).show()
                            }
                            requireActivity().supportFragmentManager.popBackStack()
                        } else {
                            handler.post {
                                Toast.makeText(activity, "반납 승인 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }

                    override fun onError(e: Exception) {
                        if (!standbyAlreadySent) {
                            handler.post {
                                Toast.makeText(activity, "반납 승인 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            handleBluetoothError(returnSheetForm, newOutstandingRentalSheet.rentalSheetDto.workerDto.id, newOutstandingRentalSheet.rentalSheetDto.leaderDto.id)
                            e.printStackTrace()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                })
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
    private fun handleBluetoothError(sheet: ReturnSheetFormDto, workerId: Long, leaderId: Long) {
        Log.d("STANDBY","STANDBY ACCESS")

        val returnSheetForm = sheet
        val toolList = returnSheetForm.toolList
        var dbHelper = DatabaseHelper(requireContext())
        val names: Pair<String, String> = Pair(dbHelper.getMembershipById(workerId).name, dbHelper.getMembershipById(leaderId).name)
        val timestamp = LocalDateTime.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var pairToolList = listOf<Pair<String,Int>>()
        for (tool in toolList) {
            val name = dbHelper.getToolById(tool.toolDtoId).name
            val count = tool.count
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }
        val detail = gson.toJson(StandbyParam(returnSheetForm.rentalSheetDtoId, names.first, names.second, timestamp, pairToolList))
        val standbySheet = ReturnSheetFormStandbySheet(sheet, timestamp)
        dbHelper.insertStandbyData(gson.toJson(standbySheet), "RETURN","STANDBY", detail)
        dbHelper.close()
    }
}