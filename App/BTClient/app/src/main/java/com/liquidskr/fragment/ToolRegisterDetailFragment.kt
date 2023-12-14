package com.liquidskr.fragment

import SharedViewModel
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
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
import com.mrsmart.standard.tool.ToolDto
import java.lang.reflect.Type

class ToolRegisterDetailFragment(tool: ToolDto) : Fragment() {

    val tool: ToolDto = tool

    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView
    private lateinit var QR_okay: TextView
    private lateinit var toolCount: TextView
    private lateinit var QR_Btn: ImageButton
    private lateinit var QR_registerText: EditText

    private lateinit var confirmBtn: ImageButton
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
        QR_okay = view.findViewById(R.id.Register_QR_Okay)
        toolCount = view.findViewById(R.id.Register_ToolCount)
        QR_Btn = view.findViewById(R.id.QR_Btn)
        QR_registerText = view.findViewById(R.id.QR_RegisterText)
        confirmBtn = view.findViewById(R.id.Register_ConfirmBtn)

        toolName.text = tool.name
        toolSpec.text = tool.spec
        QR_okay.text = ""
        var QR_context = ""

        QR_Btn.setOnClickListener {
            if (!QR_registerText.isFocused) {
                QR_registerText.requestFocus()
            }
        }
        toolCount.setOnClickListener{
            showNumberDialog(toolCount)
        }
        QR_registerText.setOnEditorActionListener { _, actionId, event ->
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                QR_context = fixCode(QR_registerText.text.toString().replace("\n", ""))
                QR_registerText.text.clear()
                QR_okay.text = "QR 등록 완료 : ${QR_context}"

                return@setOnEditorActionListener true
            }
            false
        }
        confirmBtn.setOnClickListener {
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL_FORM,"{toolId:${tool.id},toolboxId:${sharedViewModel.toolBoxId},qrcode:${QR_context},toolCount:${toolCount.text.toString().toInt()}}",object:BluetoothManager.RequestCallback{
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