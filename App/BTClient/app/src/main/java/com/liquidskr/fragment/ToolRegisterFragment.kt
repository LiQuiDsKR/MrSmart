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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolRegisterAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tool.TagAndToolboxToolLabelDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import java.lang.reflect.Type


class ToolRegisterFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var connectBtn: ImageButton

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private lateinit var QREditText: EditText
    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton
    private var runnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end
    private lateinit var welcomeMessage: TextView

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_register, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        connectBtn = view.findViewById(R.id.ConnectBtn)
        connectBtn.setOnClickListener{
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            try {
                bluetoothManager.bluetoothOpen()
                connectBtn.setImageResource(R.drawable.manager_lobby_connectionbtn)
            } catch (e: Exception) {
                Toast.makeText(context, "연결에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.setBluetoothConnectionListener(object : BluetoothManager.BluetoothConnectionListener {
            override fun onBluetoothDisconnected() {
                handler.post {
                    hidePopup()
                    connectBtn.setImageResource(R.drawable.group_11_copy)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결이 끊겼습니다.")
            }

            override fun onBluetoothConnected() {
                handler.post {
                    hidePopup()
                    connectBtn.setImageResource(R.drawable.manager_lobby_connectionbtn)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결에 성공했습니다.")
            }
        })

        QREditText = view.findViewById(R.id.QR_EditText)
        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        bluetoothManager.setBluetoothConnectionListener(object : BluetoothManager.BluetoothConnectionListener {
            override fun onBluetoothDisconnected() {
                handler.post {
                    hidePopup()
                    connectBtn.setImageResource(R.drawable.group_11_copy)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결이 끊겼습니다.")
            }

            override fun onBluetoothConnected() {
                handler.post {
                    hidePopup()
                    connectBtn.setImageResource(R.drawable.manager_lobby_connectionbtn)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결에 성공했습니다.")
            }
        })
        val databaseHelper = DatabaseHelper.getInstance()
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()

        val adapter = ToolRegisterAdapter(tools) { tool ->
            showPopup()
            recyclerView.requestFocus()

            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(Constants.BluetoothMessageType.TAG_AND_TOOLBOX_TOOL_LABEL,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    val tagAndTBT: TagAndToolboxToolLabelDto = gson.fromJson(result, type)

                    val tagQRList: MutableList<String> = mutableListOf()
                    for (tagDto in tagAndTBT.tagDtoList) {
                        if (tagDto.macaddress != null) tagQRList.add(tagDto.macaddress)
                    }
                    val fragment = ToolRegisterDetailFragment(tool.toToolDto(), tagQRList)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                override fun onError(e: Exception) {
                    handler.post{
                        Toast.makeText(activity, "공기구에 따른 선반 코드와 태그 코드를 불러오지 못했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        QREditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val label = QREditText.text.toString().replace("\n", "")
                QREditText.text.clear()
                if (label != "") {
                    try {
                        showPopup()
                        QREditText.clearFocus()
                        recyclerView.requestFocus()

                        val dbHelper = DatabaseHelper.getInstance()
                        val tool = dbHelper.getToolByTBT(label)
                        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
                        bluetoothManager.requestData(Constants.BluetoothMessageType.TAG_AND_TOOLBOX_TOOL_LABEL,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                val tagAndTBT: TagAndToolboxToolLabelDto = gson.fromJson(result, type)

                                val tagQRList: MutableList<String> = mutableListOf()
                                for (tagDto in tagAndTBT.tagDtoList) {
                                    if (tagDto.macaddress != null) tagQRList.add(tagDto.macaddress)
                                }
                                val fragment = ToolRegisterDetailFragment(tool.toToolDto(), tagQRList)
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                            override fun onError(e: Exception) {
                                hidePopup()
                                handler.post{
                                    Toast.makeText(activity, "공기구에 따른 선반 코드와 태그 코드를 불러오지 못했습니다.",Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    } catch (e:Exception) {
                        Toast.makeText(activity, "해당 선반코드로 공기구를 검색하지 못했습니다.",Toast.LENGTH_SHORT).show()
                        hidePopup()
                    }
                } else {
                    handler.post{
                        Toast.makeText(activity, "QR을 읽는 중에 문제가 발생했습니다. 다시 입력해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }

                QREditText.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        editTextName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                QREditText.requestFocus()
            }
        }

        searchBtn.setOnClickListener {
            filterByName(adapter, editTextName.text.toString())
            editTextName.clearFocus()
            QREditText.requestFocus()
        }

        bluetoothManager
        bluetoothManager.requestData(Constants.BluetoothMessageType.TEST,"{string:\"check\"}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {

            }

            override fun onError(e: Exception) {

            }
        })

        QREditText.requestFocus()
        recyclerView.adapter = adapter

        return view
    }

    fun filterByName(adapter: ToolRegisterAdapter, keyword: String) {
        val dbHelper = DatabaseHelper.getInstance()
        try {
            val newList = dbHelper.getToolsByQuery(keyword)
            adapter.updateList(newList)
        } catch(e: Exception) {
            handler.post {
                Toast.makeText(activity, "해당 검색어를 통해 공기구를 조회할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showPopup() {
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
    }
}