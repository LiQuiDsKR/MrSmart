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
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.rental.StandbyParam
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolState
import com.mrsmart.standard.tool.ToolWithCount
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ManagerOutstandingDetailFragment(outstandingRentalSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingRentalSheet.rentalSheetDto.toolList

    var outstandingSheet: OutstandingRentalSheetDto = outstandingRentalSheet

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

        returnerName.text = outstandingSheet.rentalSheetDto.workerDto.name
        workerName.text = outstandingSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingSheet.rentalSheetDto.eventTimestamp


        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        var existToolList: MutableList<RentalToolDto> = mutableListOf() // 0인 항목 미표시
        for (rentalTool in toolList) {
            if (rentalTool.outstandingCount > 0) {
                existToolList.add(rentalTool)
            }
        }

        var rentalToolList: MutableList<RentalToolDto> = outstandingSheet.rentalSheetDto.toolList.toMutableList()
        var returnToolFormList: MutableList<ReturnToolFormDto> = mutableListOf()
        var finalReturnToolFormList: MutableList<ReturnToolFormDto> = mutableListOf()
        var toolWithCountList: MutableList<ToolWithCount> = mutableListOf()

        for (a in rentalToolList) {
            toolWithCountList.add(ToolWithCount(a.toolDto.toToolDtoSQLite(), a.outstandingCount))
        }
        for (rentalTool in rentalToolList) {
            val tags = rentalTool.Tags ?: "" // Tags가 null이면 빈 문자열로 처리
            returnToolFormList.add(ReturnToolFormDto(rentalTool.id, rentalTool.toolDto.id, rentalTool.outstandingCount, ToolState.GOOD, tags))
        }
        val adapter = OutstandingDetailAdapter(recyclerView, existToolList, onSetToolStateClick = { rentalTool, counts ->
            val customModal = CustomModal(requireContext(), counts)
            customModal.setOnCountsConfirmedListener(object : CustomModal.OnCountsConfirmedListener {
                override fun onCountsConfirmed(counts: IntArray) {
                    Log.d("ManagerOutstandingDetailFragment", "Counts: ${counts.joinToString()}")
                    for (tool in rentalToolList) {
                        if (tool.id == rentalTool.id) {
                            val modifiedReturnToolFormList: MutableList<ReturnToolFormDto> = mutableListOf()
                            for (returnToolForm in returnToolFormList) {
                                if (returnToolForm.rentalToolDtoId == rentalTool.id) {
                                    if (counts[0] > 0) modifiedReturnToolFormList.add(ReturnToolFormDto(returnToolForm.rentalToolDtoId, returnToolForm.rentalToolDtoId, counts[0], ToolState.GOOD, returnToolForm.Tags))
                                    if (counts[1] > 0) modifiedReturnToolFormList.add(ReturnToolFormDto(returnToolForm.rentalToolDtoId, returnToolForm.rentalToolDtoId, counts[1], ToolState.FAULT, returnToolForm.Tags))
                                    if (counts[2] > 0) modifiedReturnToolFormList.add(ReturnToolFormDto(returnToolForm.rentalToolDtoId, returnToolForm.rentalToolDtoId, counts[2], ToolState.DAMAGE, returnToolForm.Tags))
                                    if (counts[3] > 0) modifiedReturnToolFormList.add(ReturnToolFormDto(returnToolForm.rentalToolDtoId, returnToolForm.rentalToolDtoId, counts[3], ToolState.LOSS, returnToolForm.Tags))
                                    if (counts[4] > 0) modifiedReturnToolFormList.add(ReturnToolFormDto(returnToolForm.rentalToolDtoId, returnToolForm.rentalToolDtoId, counts[4], ToolState.DISCARD, returnToolForm.Tags))
                                }
                            }
                            finalReturnToolFormList.addAll(modifiedReturnToolFormList)
                        }
                    }
                }
            })
            customModal.show()
            // 여기에 counts(배열)이 전달되었으면 좋겠어
        }, onToolCountClick = { newToolWithCount ->
            toolWithCountList = newToolWithCount
            for (rentalTool in rentalToolList) {
                val tags = rentalTool.Tags ?: "" // Tags가 null이면 빈 문자열로 처리
                returnToolFormList.add(ReturnToolFormDto(rentalTool.id, rentalTool.toolDto.id, rentalTool.outstandingCount, ToolState.GOOD, tags))
            }
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
                                for (rentalToolDto: RentalToolDto in outstandingSheet.rentalSheetDto.toolList) {
                                    toolDtoList = toolDtoList.plus(rentalToolDto.toolDto)
                                }
                                var toolIdList = listOf<Long>()
                                for (tool in toolDtoList) {
                                    toolIdList = toolIdList.plus(tool.id)
                                }
                                if (taggedTool.id in toolIdList) {
                                    val rentalToolList: MutableList<RentalToolDto> = mutableListOf()
                                    for (tool: RentalToolDto in outstandingSheet.rentalSheetDto.toolList) {
                                        var modifiedRentalTool = RentalToolDto(tool.id, tool.toolDto, tool.count, tool.outstandingCount, tool.Tags?:"null")
                                        var modifiedTag = ""
                                        if (tool.toolDto.id == taggedTool.id) {
                                            if (tool.Tags == null || tool.Tags == "") {
                                                modifiedTag = tag.macaddress
                                                handler.post {
                                                    Toast.makeText(activity, "${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                                }
                                                adapter.tagAdded(modifiedRentalTool)
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
                                        modifiedRentalTool = RentalToolDto(tool.id, tool.toolDto, tool.count, tool.outstandingCount, modifiedTag)
                                        rentalToolList.add(modifiedRentalTool)
                                    }
                                    val rentalSheet = RentalSheetDto(outstandingSheet.rentalSheetDto.id, outstandingSheet.rentalSheetDto.workerDto, outstandingSheet.rentalSheetDto.leaderDto, outstandingSheet.rentalSheetDto.approverDto, outstandingSheet.rentalSheetDto.toolboxDto, outstandingSheet.rentalSheetDto.eventTimestamp, rentalToolList)
                                    val outStandingRentalSheetDto = OutstandingRentalSheetDto(outstandingSheet.id, rentalSheet, outstandingSheet.totalCount, outstandingSheet.totalOutstandingCount,outstandingSheet.outstandingStatus)
                                    Log.d("newRentalSheet",outStandingRentalSheetDto.toString())
                                    outstandingSheet = outStandingRentalSheetDto
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
                for (toolId in adapter.selectedToolsToReturn) {
                    for (tool in outstandingSheet.rentalSheetDto.toolList) {
                        if (tool.id == toolId) {
                            for (i in adapter.selectedToolsToReturn.indices) {
                                if (tool.id == adapter.selectedToolsToReturn[i]) {
                                    val holder = recyclerView.findViewHolderForAdapterPosition(adapter.outstandingRentalTools.indexOf(tool)) as? OutstandingDetailAdapter.OutstandingRentalToolViewHolder
                                    val count = holder?.toolCount?.text.toString().toInt()
                                    for (returnToolForm in finalReturnToolFormList) {
                                        if (tool.id == returnToolForm.toolDtoId) {
                                            returnToolFormDtoList.add(ReturnToolFormDto(tool.id, tool.toolDto.id, returnToolForm.count, returnToolForm.status, returnToolForm.Tags)) //Good아닐수도
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                val returnSheetForm = ReturnSheetFormDto(outstandingSheet.rentalSheetDto.id, outstandingSheet.rentalSheetDto.workerDto.id, outstandingSheet.rentalSheetDto.approverDto.id, sharedViewModel.toolBoxId, returnToolFormDtoList)
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
                            handleBluetoothError(returnSheetForm)
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
    private fun handleBluetoothError(sheet: ReturnSheetFormDto) {
        Log.d("STANDBY","STANDBY ACCESS")

        val returnSheetForm = sheet
        val toolList = returnSheetForm.toolList
        var dbHelper = DatabaseHelper(requireContext())
        val names: Pair<String, String> = dbHelper.getNamesByRSId(returnSheetForm.rentalSheetDtoId)
        val timestamp = dbHelper.getTimestampByRSId(returnSheetForm.rentalSheetDtoId)

        var pairToolList = listOf<Pair<String,Int>>()
        for (tool in toolList) {
            val name = dbHelper.getToolById(tool.toolDtoId).name
            val count = tool.count
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }
        val detail = gson.toJson(StandbyParam(returnSheetForm.rentalSheetDtoId, names.first, names.second, timestamp, pairToolList))

        dbHelper.insertStandbyData(gson.toJson(sheet), "RETURN","STANDBY", detail)
        dbHelper.close()
    }
}