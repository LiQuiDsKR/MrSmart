package com.liquidskr.fragment

import SharedViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
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

    lateinit var scanBtn: LinearLayout
    lateinit var qrTextEdit: EditText
    lateinit var qrDisplay: TextView
    var qrcode: String = ""

    lateinit var qr_tagRegisterBtn: LinearLayout

    lateinit var qr_checkScanBtn: LinearLayout
    lateinit var qr_checkScanEdit: EditText

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register_detail, container, false)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
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

        confirmBtn = view.findViewById(R.id.confirmBtn)

        var adapter = ToolRegisterTagDetailAdapter(emptyList())

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                if (result == "null") {
                    qrDisplay.text = "미등록"
                } else {
                    qrDisplay.text = result
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })

        scanBtn.setOnClickListener {
            qrTextEdit.text.clear()
            qrTextEdit.requestFocus()
            qrDisplay.text = "인식 중.."
        }

        qr_checkScanBtn.setOnClickListener{
            qr_checkScanEdit.text.clear()
            qr_checkScanEdit.requestFocus()
        }

        qr_tagRegisterBtn.setOnClickListener {
            val fragment = ToolRegisterTagDetailFragment(tool, emptyList())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        qr_checkScanEdit.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                qrcode = fixCode(qr_checkScanEdit.text.toString().replace("\n", ""))
                val fragment = ToolRegisterTagDetailFragment(tool, listOf("11111","12345")) // bluetooth로 받아야함
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit()
                qr_checkScanEdit.text.clear()
                return@setOnEditorActionListener true
            }

            false
        }

        qrTextEdit.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                qrcode = fixCode(qrTextEdit.text.toString().replace("\n", ""))
                // qrcode가 이미 쓰인건지 체크
                qrDisplay.text = "${qrcode}"
                qrTextEdit.text.clear()
                qrTextEdit.clearFocus()
                return@setOnEditorActionListener true
            }

            false
        }

        confirmBtn.setOnClickListener {
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL_FORM,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId},\"qrcode\":\"${qrcode}\"}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    Toast.makeText(requireContext(), "공구 등록 완료", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
            requireActivity().supportFragmentManager.popBackStack()
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
    private fun showNumberDialog(textView: TextView) {
        val builder = AlertDialog.Builder(textView.context)
        builder.setTitle("공구 등록 개수")
        val input = NumberPicker(textView.context)
        input.minValue = 0
        input.maxValue = 999
        input.wrapSelectorWheel = false
        input.value = textView.text.toString().toInt()

        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val newValue = input.value.toString()
            // 여기서 숫자 값을 처리하거나 다른 작업을 수행합니다.
            textView.text = newValue
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}