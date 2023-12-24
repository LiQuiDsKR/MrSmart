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
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolState
import java.lang.reflect.Type

class ManagerOutstandingDetailFragment(outstandingSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingSheet.rentalSheetDto.toolList

    var outstandingSheet: OutstandingRentalSheetDto = outstandingSheet

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    lateinit var qrEditText: EditText
    lateinit var qrcodeBtn: LinearLayout

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager

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

        returnerName.text = "반납자: " + outstandingSheet.rentalSheetDto.workerDto.name
        workerName.text = "대여자: " + outstandingSheet.rentalSheetDto.workerDto.name
        leaderName.text = "리더: " + outstandingSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = "대여일시: " + outstandingSheet.rentalSheetDto.eventTimestamp

        var sheetForTag: List<RentalRequestToolDto> = listOf()

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
                    for (rentalToolDto: RentalToolDto in outstandingSheet.rentalSheetDto.toolList) {
                        toolDtoList = toolDtoList.plus(rentalToolDto.toolDto)
                    }
                    var toolIdList = listOf<Long>()
                    for (tool in toolDtoList) {
                        toolIdList = toolIdList.plus(tool.id)
                    }

                    if (taggedTool.id in toolIdList) {
                        val rentalRequestToolDtoList: MutableList<RentalRequestToolDto> = mutableListOf()
                        for (tool: RentalToolDto in outstandingSheet.rentalSheetDto.toolList) {
                            var modifiedRentalRequestTool = RentalRequestToolDto(tool.id, tool.toolDto, tool.count, tool.Tags)
                            if (tool.toolDto.id == taggedTool.id) {
                                var modifiedTag = ""
                                if (tool.Tags == null) {
                                    modifiedTag = tag
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(activity, "${taggedTool.name} 에 ${tag} 가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
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
                                modifiedRentalRequestTool = RentalRequestToolDto(tool.id, tool.toolDto, tool.count, modifiedTag)
                                //rentalRequestToolDtoList.add(modifiedRentalRequestTool)
                                sheetForTag = sheetForTag.plus(modifiedRentalRequestTool)
                            }
                        }


                        val outStandingRentalSheetDto = OutstandingRentalSheetDto(outstandingSheet.id, outstandingSheet.rentalSheetDto, outstandingSheet.totalCount, outstandingSheet.totalOutstandingCount,outstandingSheet.outstandingStatus)
                        Log.d("a",outStandingRentalSheetDto.toString())
                        outstandingSheet = outStandingRentalSheetDto
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
            recyclerView.adapter?.let { adapter ->
                if (adapter is OutstandingDetailAdapter) {
                    val returnToolFormDtoList: MutableList<ReturnToolFormDto> = mutableListOf()
                    for (toolId in adapter.selectedToolsToReturn) {
                        for (tool in outstandingSheet.rentalSheetDto.toolList) {
                            if (tool.id == toolId) {
                                for (i in adapter.outstandingRentalTools.indices) {
                                    if (tool.id == adapter.selectedToolsToReturn[i]) {
                                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.outstandingRentalTools.indexOf(tool)) as? OutstandingDetailAdapter.OutstandingRentalToolViewHolder
                                        val count = holder?.toolCount?.text.toString().toInt()
                                        var currentTags = ""
                                        for (currenttool in sheetForTag) {
                                            if (currenttool.toolDto.id == tool.toolDto.id) {
                                                currentTags = currenttool.Tags.toString()
                                            }
                                        }
                                        returnToolFormDtoList.add(ReturnToolFormDto(tool.id, tool.toolDto.id, count, ToolState.GOOD, currentTags)) //
                                    }
                                }
                            }
                        }
                    }
                    val returnSheetForm = ReturnSheetFormDto(outstandingSheet.rentalSheetDto.id, outstandingSheet.rentalSheetDto.workerDto.id, outstandingSheet.rentalSheetDto.approverDto.id, sharedViewModel.toolBoxId, returnToolFormDtoList)
                    bluetoothManager.requestData(RequestType.RETURN_SHEET_FORM, gson.toJson(returnSheetForm), object:
                        BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
                            Log.d("asdf","반납 승인 완료")
                            requireActivity().supportFragmentManager.popBackStack()
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = OutstandingDetailAdapter(toolList)
        recyclerView.adapter = adapter

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
        dbHelper.insertStandbyData(gson.toJson(json), "RENTAL","STANDBY" )
        dbHelper.close()
    }
}