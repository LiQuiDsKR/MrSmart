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
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
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
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }

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
                whenDisconnected()
                Log.d("BluetoothStatus", "Bluetooth 연결이 끊겼습니다.")
            }
        })
        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()

        val adapter = ToolRegisterAdapter(tools) { tool ->

            editTextName.clearFocus()
            editTextName.isClickable = false
            editTextName.isFocusable = false
            recyclerView.requestFocus()

            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.TAG_AND_TOOLBOX_TOOL_LABEL,"{\"toolId\":${tool.id},\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
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
            disableAllClickableViews(view)
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            disableAllClickableViews(view)
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            disableAllClickableViews(view)
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            disableAllClickableViews(view)
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLogin", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        searchBtn.setOnClickListener {
            filterByName(adapter, editTextName.text.toString())
            editTextName.clearFocus()
        }

        editTextName.setOnEditorActionListener { _, actionId, event ->
            Log.d("tst","textEditted")
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val label = editTextName.text.toString().replace("\n", "")
                try {
                    val dbHelper = DatabaseHelper(requireContext())
                    val tool = dbHelper.getToolByTBT(label)
                    val fragment = ToolRegisterDetailFragment(tool.toToolDto(), listOf())
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                } catch(e:Exception) {
                    Toast.makeText(activity, "해당 선반코드로 공기구를 검색하지 못했습니다.",Toast.LENGTH_SHORT).show()
                }
                editTextName.text.clear()
                editTextName.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        editTextName.requestFocus()
        recyclerView.adapter = adapter

        return view
    }

    fun whenDisconnected () {
        Log.d("bluetooth_","Disconnected3")
        connectBtn.setImageResource(R.drawable.group_11_copy)
        hidePopup()
        handler.post {
            Toast.makeText(activity, "블루투스 연결이 끊겼습니다. 다시 연결해주세요.",Toast.LENGTH_SHORT).show()
        }
    }

    fun disableAllClickableViews(rootView: View) {
        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val childView = rootView.getChildAt(i)
                disableAllClickableViews(childView)
            }
        } else {
            rootView.isClickable = false
        }
    }

    fun filterByName(adapter: ToolRegisterAdapter, keyword: String) {
        val dbHelper = DatabaseHelper(requireContext())
        try {
            val newList = dbHelper.getToolsByQuery(keyword)
            adapter.updateList(newList)
        } catch(e: Exception) {
            handler.post {
                Toast.makeText(activity, "해당 검색어를 통해 공기구를 조회할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun scheduleTask(delaySeconds: Long, task: () -> Unit) {
        runnable = Runnable {
            task.invoke()
        }
        handler.postDelayed(runnable!!, delaySeconds)
    }
    fun cancelTimer() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
        sharedViewModel.qrScannerText = ""
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