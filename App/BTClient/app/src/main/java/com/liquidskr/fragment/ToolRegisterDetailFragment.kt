package com.liquidskr.fragment

import SharedViewModel
import android.content.Context
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
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.liquidskr.btclient.ToolRegisterTagDetailAdapter
import com.mrsmart.standard.tool.ToolDto
import java.lang.reflect.Type

class ToolRegisterDetailFragment(tool: ToolDto) : Fragment() {

    val tool: ToolDto = tool

    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView
    private lateinit var context: Context

    lateinit var scanBtn: LinearLayout
    lateinit var qrTextEdit: EditText
    lateinit var qrDisplay: TextView
    var tbt_qrcode: String = ""

    lateinit var qr_tagRegisterBtn: LinearLayout

    lateinit var qr_checkScanBtn: LinearLayout
    lateinit var qr_checkScanEdit: EditText

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
            bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL_FORM,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId},\"qrcode\":\"${tbt_qrcode}\"}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        handler.post {
                            Toast.makeText(context, "공기구 등록 완료", Toast.LENGTH_SHORT).show()
                        }
                        try {
                            val dbHelper = DatabaseHelper(context)
                            dbHelper.updateQRCodeById(tool.id, tbt_qrcode) // << 왜안되는지
                        } catch (e:Exception) {
                            handler.post {
                                Toast.makeText(context, "라벨 정보가 DB에 저장되지 않았습니다.", Toast.LENGTH_SHORT).show()
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                                Log.d("TST", e.toString())
                            }
                        }
                    } else if ("already exists!" in result) {
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
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        val handler = Handler(Looper.getMainLooper())
        context = requireContext()
        toolName = view.findViewById(R.id.Register_ToolName)
        toolSpec = view.findViewById(R.id.Register_ToolSpec)

        toolName.text = tool.name
        toolSpec.text = tool.spec
        scanBtn = view.findViewById(R.id.qr_scanBtn)
        qrTextEdit = view.findViewById(R.id.qr_textEdit)
        qrDisplay = view.findViewById(R.id.qr_display)

        qr_tagRegisterBtn = view.findViewById(R.id.qr_tagRegisterBtn)

        qr_checkScanBtn = view.findViewById(R.id.qr_checkScanBtn)
        qr_checkScanEdit = view.findViewById(R.id.qr_checkScanEdit)
        backButton = view.findViewById(R.id.backButton)

        confirmBtn = view.findViewById(R.id.confirmBtn)

        var adapter = ToolRegisterTagDetailAdapter(emptyList())

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
        scanBtn.setOnClickListener {
            qrTextEdit.text.clear()
            qrTextEdit.requestFocus()
            qrDisplay.text = "인식 중.."
        }

        qr_checkScanBtn.setOnClickListener{
            qr_checkScanEdit.text.clear()
            qr_checkScanEdit.requestFocus()
            qr_checkScanBtn.setBackgroundResource(R.drawable.qr_check)
        }

        qr_tagRegisterBtn.setOnClickListener {
            val fragment = ToolRegisterTagDetailFragment(tool, emptyList(), "")
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        qr_checkScanEdit.setOnEditorActionListener { _, actionId, event ->
            qr_checkScanBtn.setBackgroundResource(R.drawable.qr_check_ready)
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val qrcode = fixCode(qr_checkScanEdit.text.toString().replace("\n", ""))
                bluetoothManager.requestData(RequestType.TAG_LIST,"{\"tag\":\"${qrcode}\"}",object:BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        if (result == "null") {
                            handler.post {
                                Toast.makeText(context, "해당 태그는 등록되어 있지 않아 조회할 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val tagList: List<String> = gson.fromJson(result, type)
                            val fragment = ToolRegisterTagDetailFragment(tool, tagList, qrcode) // bluetooth로 받아야함
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .addToBackStack(null)
                                .commit()
                            qr_checkScanEdit.text.clear()
                        }
                    }
                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }
                })

                return@setOnEditorActionListener true
            }

            false
        }

        qrTextEdit.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                tbt_qrcode = fixCode(qrTextEdit.text.toString().replace("\n", ""))
                // qrcode가 이미 쓰인건지 체크
                qrDisplay.text = "${tbt_qrcode}"
                qrTextEdit.text.clear()
                qrTextEdit.clearFocus()
                return@setOnEditorActionListener true
            }

            false
        }

        confirmBtn.setOnClickListener {
            showBluetoothModal("알림","라벨 정보를 등록하시겠습니까?")
        }
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