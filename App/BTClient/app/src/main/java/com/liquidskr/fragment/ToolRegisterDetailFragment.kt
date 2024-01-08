package com.liquidskr.fragment

import SharedViewModel
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
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
import com.liquidskr.btclient.RequestType
import com.liquidskr.btclient.ToolRegisterTagDetailAdapter
import com.mrsmart.standard.tool.ToolDto
import java.lang.reflect.Type

class ToolRegisterDetailFragment(var tool: ToolDto, var tagList: List<String>) : Fragment() {

    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView
    private lateinit var context: Context

    private lateinit var check_tag: ImageButton
    private lateinit var check_label: ImageButton

    private lateinit var scannerReceiver: LinearLayout

    lateinit var qrTextEdit: EditText
    lateinit var qrDisplay: TextView
    var tbt_qrcode: String = ""

    lateinit var recyclerView: RecyclerView
    lateinit var qrSearchText: EditText

    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val lobbyActivity: LobbyActivity
        get() = requireActivity() as LobbyActivity

    private fun showBluetoothModal(title: String, content: String) {
        lobbyActivity.showBluetoothModal(title, content, bluetoothModalListener)
    }
    private val bluetoothModalListener = object : LobbyActivity.BluetoothModalListener {
        override fun onConfirmButtonClicked() {
            val handler = Handler(Looper.getMainLooper())
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            val tagGroup = if (tagList.size > 0) tagList[0] else ""
            val tagLists: MutableList<String> = mutableListOf()
            for (tag in tagList) {
                tagLists.add("\"${tag}\"")
            }
            bluetoothManager.requestData(RequestType.TAG_AND_TOOLBOX_TOOL_LABEL_FORM,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId},\"qrcode\":\"${tbt_qrcode}\",\"tagGroup\":\"${tagGroup}\",\"tagList\":${tagLists}}" ,object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        try {
                            val dbHelper = DatabaseHelper(context)
                            dbHelper.updateQRCodeById(tool.id, tbt_qrcode, sharedViewModel.toolBoxId)
                            handler.post {
                                Toast.makeText(context, "공기구 등록 완료", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e:Exception) {
                            handler.post {
                                Toast.makeText(context, "라벨 정보가 DB에 저장되지 않았습니다.", Toast.LENGTH_SHORT).show()
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                                Log.d("TST", e.toString())
                            }
                        }
                    } else if ("ERROR_ALREADY_EXIST" in result) {
                        handler.post {
                            Toast.makeText(context, "이미 다른 공기구에 등록된 라벨입니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handler.post {
                            Toast.makeText(context, "알 수 없는 오류 발생", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(e: Exception) {
                    handler.post {
                        Toast.makeText(context, "공기구 등록 실패", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
            })
            requireActivity().supportFragmentManager.popBackStack()
        }

        override fun onCancelButtonClicked() {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register_detail, container, false)

        val handler = Handler(Looper.getMainLooper())
        context = requireContext()
        scannerReceiver = view.findViewById(R.id.scannerReceiver)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        toolName = view.findViewById(R.id.Register_ToolName)
        toolSpec = view.findViewById(R.id.Register_ToolSpec)

        check_label = view.findViewById(R.id.check_label)
        check_tag = view.findViewById(R.id.check_tag)

        toolName.text = tool.name
        toolSpec.text = tool.spec
        qrTextEdit = view.findViewById(R.id.qr_textEdit)
        qrDisplay = view.findViewById(R.id.qr_display)

        recyclerView = view.findViewById(R.id.qrRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        var adapter = ToolRegisterTagDetailAdapter(tagList)

        backButton = view.findViewById(R.id.backButton)

        confirmBtn = view.findViewById(R.id.confirmBtn)

        qrDisplay.text = "미등록"
        try {
            val dbHepler = DatabaseHelper(requireContext())
            val label = dbHepler.getTBTByToolId(tool.id)
            qrDisplay.text = label
            tbt_qrcode = label
        } catch(e: Exception) {
            e.printStackTrace()
            handler.post {
                Toast.makeText(context, "공기구의 라벨 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        var isLabelMode = true
        check_label.setOnClickListener {
            isLabelMode = !isLabelMode
            if (isLabelMode) {
                check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_on)
                check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
            }
        }
        check_tag.setOnClickListener {
            isLabelMode = !isLabelMode
            check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
            check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_on)

        }

        qrTextEdit.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val qrcode = qrTextEdit.text.toString().replace("\n", "")
                if (isLabelMode) { // Label 모드
                    qrTextEdit.requestFocus()
                    tbt_qrcode = qrcode
                    // qrcode가 이미 쓰인건지 체크
                    qrDisplay.text = "${tbt_qrcode}"
                } else { // Tag 모드
                    qrTextEdit.requestFocus()
                    var list = adapter.qrcodes.toMutableList()
                    if (!(qrcode in list)) {
                        list.add(qrcode)
                        adapter.updateList(list)
                        tagList = adapter.qrcodes
                    } else {
                        adapter.qrCheck(qrcode)
                    }
                }
                qrTextEdit.text.clear()
                qrTextEdit.requestFocus()

                return@setOnEditorActionListener true
            }
            false
        }

        qrTextEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                qrTextEdit.requestFocus()
            }
        }
        confirmBtn.setOnClickListener {
            showBluetoothModal("알림","라벨 정보를 등록하시겠습니까?")
        }

        recyclerView.adapter = adapter
        qrTextEdit.requestFocus()
        handler.postDelayed ({
            qrTextEdit.requestFocus()
        },500)
        return view
    }

    private fun fixCode(input: String): String {
        val typoMap = mapOf(
            'ㅁ' to 'A',
            'ㅠ' to 'B',
            'ㅊ' to 'C',
            'ㅇ' to 'D',
            'ㄷ' to 'E',
            'ㄹ' to 'F',
            'ㅎ' to 'G',
            'ㅗ' to 'H',
            'ㅑ' to 'I',
            'ㅓ' to 'J',
            'ㅏ' to 'K',
            'ㅣ' to 'L',
        )
        val correctedText = StringBuilder()
        for (char in input) {
            val correctedChar = typoMap[char] ?: char
            correctedText.append(correctedChar)
        }
        return correctedText.toString()
    }
}