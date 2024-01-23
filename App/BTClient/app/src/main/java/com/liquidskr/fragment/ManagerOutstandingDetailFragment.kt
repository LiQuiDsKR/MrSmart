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
import com.mrsmart.standard.rental.OutstandingState
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.standby.StandbyParam
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.standby.ReturnSheetFormStandbySheet
import com.mrsmart.standard.tool.RentalToolWithCount
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolWithCount
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ManagerOutstandingDetailFragment(private var outstandingRentalSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var RentalToolWithCountList: MutableList<RentalToolWithCount> = mutableListOf()

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressText: TextView
    private var isPopupVisible = false // UI블로킹 end

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    lateinit var qrEditText: EditText
    private lateinit var backButton: ImageButton

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
        backButton = view.findViewById(R.id.backButton)

        returnerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        workerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingRentalSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingRentalSheet.rentalSheetDto.eventTimestamp

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        var returnToolFormList: MutableList<ReturnToolFormDto> = mutableListOf()

        for (rentalTool in outstandingRentalSheet.rentalSheetDto.toolList) {
            if (rentalTool.outstandingCount > 0) {
                RentalToolWithCountList.add(RentalToolWithCount(rentalTool, rentalTool.outstandingCount))
            }
        }

        for (rtwc in RentalToolWithCountList) {
            val tags = rtwc.rentalTool.Tags ?: ""
            returnToolFormList.add(ReturnToolFormDto(rtwc.rentalTool.id, rtwc.rentalTool.toolDto.id, tags, rtwc.count,0,0,0,""))
        }


        var finalToolStateList: MutableList<Pair<Long,ReturnToolFormDto>> = mutableListOf()
        for (rtwc in RentalToolWithCountList) { // rtwc means RentalToolWithCount
            val tags = rtwc.rentalTool.Tags ?: ""
            finalToolStateList.add(Pair(rtwc.rentalTool.toolDto.id, ReturnToolFormDto(rtwc.rentalTool.id, rtwc.rentalTool.toolDto.id, tags, rtwc.rentalTool.outstandingCount,0,0,0, "")))
        }

        val adapter = OutstandingDetailAdapter(recyclerView, RentalToolWithCountList, onSetToolStateClick = { rentalToolWithCount ->
            var cnt = 0
            for (rtwc in RentalToolWithCountList) {
                if (rtwc.rentalTool.toolDto.id == rentalToolWithCount.rentalTool.toolDto.id) {
                    cnt = rtwc.rentalTool.outstandingCount
                }
            }
            val customModal = CustomModal(requireContext(), cnt)
            customModal.setOnCountsConfirmedListener(object : CustomModal.OnCountsConfirmedListener {
                override fun onCountsConfirmed(counts: IntArray, comment: String) {
                    for (rtwc in RentalToolWithCountList) {
                        if (rtwc.rentalTool.id == rentalToolWithCount.rentalTool.id) {
                            // finalToolStateList = finalToolStateList.filter { it.first != rtwc.rentalTool.toolDto.id }.toMutableList() // 이미 toolState를 등록한 ToolId라면 해당 항목 제거
                            var sum = 0
                            for (count in counts) {
                                sum += count
                            }
                            returnToolFormList = returnToolFormList.filter{ it.toolDtoId != rtwc.rentalTool.toolDto.id }.toMutableList()
                            if (sum != 0) {
                                val goodCnt = counts[0]
                                val faultCnt = counts[1]
                                val damageCnt = counts[2]
                                val lossCnt = counts[3]
                                // val discardCnt = counts[4]

                                val tags = rtwc.rentalTool.Tags ?: ""
                                var returnToolForm = ReturnToolFormDto(rtwc.rentalTool.id, rtwc.rentalTool.toolDto.id, tags, goodCnt, faultCnt, damageCnt, lossCnt, comment)
                                returnToolFormList.add(returnToolForm)
                            }
                            updateToolState(rtwc.rentalTool.toolDto.id, counts)
                            /*
                            if (counts[0] > 0) toolStateParamList.add(ToolStateParam(rtwc.rentalTool.toolDto.id, ToolState.GOOD, counts[0]))
                            if (counts[1] > 0) toolStateParamList.add(ToolStateParam(rtwc.rentalTool.toolDto.id, ToolState.FAULT, counts[1]))
                            if (counts[2] > 0) toolStateParamList.add(ToolStateParam(rtwc.rentalTool.toolDto.id, ToolState.DAMAGE, counts[2]))
                            if (counts[3] > 0) toolStateParamList.add(ToolStateParam(rtwc.rentalTool.toolDto.id, ToolState.LOSS, counts[3]))
                            if (counts[4] > 0) toolStateParamList.add(ToolStateParam(rtwc.rentalTool.toolDto.id, ToolState.DISCARD, counts[4]))
                            */

                        }
                    }
                }
            })
            customModal.show()
        })
        recyclerView.adapter = adapter

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
                                for (rtwc in adapter.outstandingRentalToolWithCounts) {
                                    if (rtwc.rentalTool.toolDto.id == taggedTool.id) {
                                        rtwc.rentalTool.Tags = tag.macaddress
                                        handler.post {
                                            adapter.tagAdded(taggedTool.id, tag.macaddress) // 태그 넣는거 안됨
                                            lateinit var newReturnToolForm: ReturnToolFormDto
                                            for (returnToolForm in returnToolFormList) {
                                                if (returnToolForm.toolDtoId == taggedTool.id) {
                                                    newReturnToolForm = returnToolForm
                                                }
                                            }
                                            returnToolFormList = returnToolFormList.filter { it.toolDtoId != taggedTool.id }.toMutableList()
                                            val newTag = tag.macaddress
                                            returnToolFormList.add(ReturnToolFormDto(newReturnToolForm.rentalToolDtoId, newReturnToolForm.toolDtoId, newTag, newReturnToolForm.goodCount, newReturnToolForm.faultCount,newReturnToolForm.damageCount,newReturnToolForm.lossCount, newReturnToolForm.comment))

                                            adapter.updateList(adapter.outstandingRentalToolWithCounts)
                                            Toast.makeText(requireContext(),"${taggedTool.name} 에 ${tag.macaddress} 가 확인되었습니다.",Toast.LENGTH_SHORT).show()
                                        }
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

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {
            confirmBtn.isFocusable = false
            confirmBtn.isClickable = false

            var standbyAlreadySent = false
            if (adapter is OutstandingDetailAdapter) {
                if (adapter.selectedToolsToReturn.isNotEmpty()) {
                    showPopup() // UI 블로킹
                    val sheet = outstandingRentalSheet
                    returnToolFormList = returnToolFormList.filter { adapter.selectedToolsToReturn.contains(it.toolDtoId) }.toMutableList()
                    val returnSheetForm = ReturnSheetFormDto(sheet.rentalSheetDto.id, sheet.rentalSheetDto.workerDto.id, sharedViewModel.loginManager.id, sharedViewModel.toolBoxId, returnToolFormList)
                    bluetoothManager.requestData(RequestType.RETURN_SHEET_FORM, gson.toJson(returnSheetForm), object:
                        BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
                            if (result == "good") {
                                hidePopup() // UI 블로킹
                                try {
                                    val dbHelper = DatabaseHelper(requireContext())
                                    dbHelper.updateOutstandingStatusBySheetId(outstandingRentalSheet.id)
                                } catch (e: Exception) {
                                    Log.d("outstaning", "승인한 반납 시트는 보류 목록에 없습니다.")
                                }
                                handler.post {
                                    Toast.makeText(activity, "반납 승인 완료", Toast.LENGTH_SHORT).show()
                                }
                                requireActivity().supportFragmentManager.popBackStack()
                            } else {
                                hidePopup() // UI 블로킹
                                handler.post {
                                    Toast.makeText(activity, "반납 승인 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        override fun onError(e: Exception) {
                            if (!standbyAlreadySent) {
                                hidePopup() // UI 블로킹
                                handler.post {
                                    Toast.makeText(activity, "반납 승인 실패, 보류항목에 추가했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                handleBluetoothError(returnSheetForm, outstandingRentalSheet.rentalSheetDto.workerDto.id, outstandingRentalSheet.rentalSheetDto.leaderDto.id)
                                e.printStackTrace()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                    })
                } else {
                    handler.post {
                        Toast.makeText(requireContext(), "공기구를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            standbyAlreadySent = true
        }
        handler.postDelayed({
            qrEditText.requestFocus()
        }, 200)
        return view
    }

    fun updateToolState(toolId: Long, counts: IntArray) {
        val myAdapter = recyclerView.adapter
        if (myAdapter is OutstandingDetailAdapter) myAdapter.updateToolState(toolId, counts)
    }

    private fun fixCode(input: String): String {
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
            val count = tool.goodCount + tool.faultCount + tool.damageCount + tool.lossCount
            val pair = Pair(name, count)
            pairToolList = pairToolList.plus(pair)
        }
        val detail = gson.toJson(StandbyParam(returnSheetForm.rentalSheetDtoId, names.first, names.second, timestamp, pairToolList))
        val standbySheet = ReturnSheetFormStandbySheet(sheet, timestamp)
        dbHelper.insertStandbyData(gson.toJson(standbySheet), "RETURN","STANDBY", detail)


        var processedToolList: MutableList<Pair<Long, Int>> = mutableListOf()
        for (returnToolForm in sheet.toolList) {
            val returnToolCnt = returnToolForm.goodCount + returnToolForm.faultCount + returnToolForm.damageCount + returnToolForm.lossCount
            processedToolList.add(Pair(returnToolForm.toolDtoId, returnToolCnt))
        }

        var leftToolList: MutableList<RentalToolDto> = mutableListOf()
        var outstandingCount = 0
        for (rentalTool in outstandingRentalSheet.rentalSheetDto.toolList) {
            for (processedTool in processedToolList) {
                if (processedTool.first == rentalTool.toolDto.id) {
                    val tags = rentalTool.Tags ?: ""
                    val leftRentalTool = RentalToolDto(rentalTool.id, rentalTool.toolDto, rentalTool.count, (rentalTool.outstandingCount - processedTool.second), tags)
                    if (leftRentalTool.outstandingCount > 0) {
                        leftToolList.add(leftRentalTool)
                        outstandingCount += leftRentalTool.outstandingCount
                    }
                }
            }
        }
        val os = outstandingRentalSheet
        val rs = os.rentalSheetDto // shorten
        val rentalSheet = RentalSheetDto(rs.id, rs.workerDto, rs.leaderDto, rs.approverDto, rs.toolboxDto, rs.eventTimestamp, leftToolList)
        val newOutstandingRentalSheet = OutstandingRentalSheetDto(os.id, rentalSheet, os.totalCount, outstandingCount,OutstandingState.READY)
        try {
            dbHelper.insertOutstandingData(os.id, gson.toJson(rentalSheet), os.totalCount, outstandingCount, OutstandingState.READY.name, gson.toJson(newOutstandingRentalSheet))
        } catch (e: Exception) {
            handler.post {
                Toast.makeText(requireContext(), "승인하지 않은 공기구를 DB에 다시 저장하는데 실패했습니다.",Toast.LENGTH_SHORT).show()
            }
        }
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