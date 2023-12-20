package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
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
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.liquidskr.btclient.ToolRegisterTagDetailAdapter
import com.mrsmart.standard.tool.ToolDto
import java.lang.reflect.Type

class ToolRegisterTagDetailFragment(tool: ToolDto, tagList: List<String>) : Fragment() {

    val tool: ToolDto = tool
    val tagList: List<String> = tagList

    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView

    lateinit var qrCheckBtn: LinearLayout
    lateinit var qrAddBtn: LinearLayout
    lateinit var qrSearchText: EditText

    lateinit var recyclerView: RecyclerView
    var qrcode: String = ""

    private lateinit var confirmBtn: LinearLayout
    private lateinit var bluetoothManager: BluetoothManager

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register_tag_detail, container, false)
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        toolName = view.findViewById(R.id.Register_ToolName)
        toolSpec = view.findViewById(R.id.Register_ToolSpec)

        qrCheckBtn = view.findViewById(R.id.qrCheckBtn)
        qrAddBtn = view.findViewById(R.id.qrAddBtn)
        qrSearchText = view.findViewById(R.id.qr_search)

        toolName.text = tool.name
        toolSpec.text = tool.spec
        confirmBtn = view.findViewById(R.id.confirmBtn)
        recyclerView = view.findViewById(R.id.qrRecyclerView)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        var adapter = ToolRegisterTagDetailAdapter(tagList)

        qrCheckBtn.setOnClickListener {
            qrSearchText.text.clear()
            qrSearchText.requestFocus()

            Log.d("tagDetail", "QR Check Btn")
        }

        qrSearchText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                qrcode = fixCode(qrSearchText.text.toString().replace("\n", ""))

                Log.d("tagDetail", qrcode)
                adapter.qrCheck(qrcode)

                qrSearchText.text.clear()
                return@setOnEditorActionListener true
            }

            false
        }

        confirmBtn.setOnClickListener {
            val tagList = gson.toJson(adapter.qrcodes)
            var dbHelper = DatabaseHelper(requireContext())
            val tagGroup = dbHelper.getTagGroupByTag(adapter.qrcodes[0])
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.TAG_FORM,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId},\"tagGroup\":\"${tagGroup}\",\"tagList\":${tagList}}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    Toast.makeText(requireContext(), "공구 등록 완료", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
            requireActivity().supportFragmentManager.popBackStack()
        }

        qrAddBtn.setOnClickListener {
            var list = adapter.qrcodes.toMutableList()
            list.add("")
            adapter.updateList(list)
        }

        recyclerView.adapter = adapter
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