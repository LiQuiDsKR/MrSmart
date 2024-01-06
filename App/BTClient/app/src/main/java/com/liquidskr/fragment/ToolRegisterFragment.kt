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
import android.widget.ProgressBar
import android.widget.TextView
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
    private var runnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end

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

        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val databaseHelper = DatabaseHelper(requireContext())
        val tools: List<ToolDtoSQLite> = databaseHelper.getAllTools()

        val adapter = ToolRegisterAdapter(tools) { tool ->
            val fragment = ToolRegisterDetailFragment(tool.toToolDto(), listOf())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        searchBtn.setOnClickListener {
            filterByName(adapter, editTextName.text.toString())
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
                        .replace(R.id.fragmentContainerView, fragment)
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
    fun handleKeyEvent(keyCode: Int) {
        if (sharedViewModel.qrScannerText == "") {
            scheduleTask(300) {
                try {
                    val dbHelper = DatabaseHelper(requireContext())
                    val fragment = ToolRegisterDetailFragment(dbHelper.getToolByTBT(sharedViewModel.qrScannerText).toToolDto(), listOf())
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