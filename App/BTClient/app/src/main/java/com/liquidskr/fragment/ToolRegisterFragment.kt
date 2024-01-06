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
import android.widget.Toast
import androidx.core.view.KeyEventDispatcher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.MyScannerListener
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.liquidskr.btclient.ToolRegisterAdapter
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import java.lang.reflect.Type
import java.security.Key


class ToolRegisterFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var confirmBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private var keyEventDispatcher: KeyEventDispatcher? = null



    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    private var active = false
    val listener: MyScannerListener.Listener = object : MyScannerListener.Listener {
        override fun onTextFinished() {
            if (!active || (sharedViewModel.qrScannerText.length == 0)) {
                return
            }

            val dbHelper = DatabaseHelper(requireContext())
            val tool = dbHelper.getToolByTBT(sharedViewModel.qrScannerText)
            val fragment = ToolRegisterDetailFragment(tool.toToolDto())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
            sharedViewModel.qrScannerText = ""
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())
        val gson = Gson()
        val view = inflater.inflate(R.layout.fragment_tool_register, container, false)

        active = true
        val lobbyActivity = requireActivity() as LobbyActivity
        lobbyActivity.setListener(listener)
        lobbyActivity.toolReturnFragment = this


        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        editTextName.setOnEditorActionListener { _, actionId, event ->
            Log.d("Keycode", actionId.toString())
            // 키 이벤트를 소비하고 처리하지 않음
            requireActivity().dispatchKeyEvent(event)
            false
        }

        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()
        val adapter = ToolRegisterAdapter(tools) { tool ->
            val fragment = ToolRegisterDetailFragment(tool.toToolDto())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        searchBtn.setOnClickListener {
            filterByName(adapter, tools, editTextName.text.toString())

        }

        recyclerView.adapter = adapter

        return view
    }

    override fun onDestroyView() {
        active = false
        super.onDestroyView()
    }

    fun filterByName(adapter: ToolRegisterAdapter, tools: List<ToolDtoSQLite>, keyword: String) {
        val newList: MutableList<ToolDtoSQLite> = mutableListOf()
        for (tool in tools) {
            if (keyword in tool.name) {
                newList.add(tool)
            }
        }
        adapter.updateList(newList)
    }
    fun handleKeyEvent(keyCode: Int) {
        if (sharedViewModel.qrScannerText == "") {
            scheduleTask(300) {
                try {
                    val dbHelper = DatabaseHelper(requireContext())
                    val fragment = ToolRegisterDetailFragment(dbHelper.getToolByTBT(sharedViewModel.qrScannerText).toToolDto())
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit()
                    sharedViewModel.qrScannerText = ""
                } catch (e:Exception) {
                    handler.post {
                        Toast.makeText(requireContext(), "입력한 품목코드에 해당하는 공기구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    sharedViewModel.qrScannerText = ""
                }
            }
        }
        val num: Int
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            num = keyCode - KeyEvent.KEYCODE_0
            sharedViewModel.qrScannerText += num.toString()
        } else {
            cancelTimer()
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
}