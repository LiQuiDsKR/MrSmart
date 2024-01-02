package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import java.lang.reflect.Type

class WorkerReturnListFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager
    lateinit var searchSheetEdit: EditText
    lateinit var sheetSearchBtn: ImageButton
    lateinit var qrCodeBtn: LinearLayout
    lateinit var qrEditText: EditText
    lateinit var outStandingRentalSheetList: List<OutstandingRentalSheetDto>
    var selectedCategory = "리더명"
    val gson = Gson()

    interface KeyListener {
        fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    }
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        qrCodeBtn = view.findViewById(R.id.QRcodeBtn)
        qrEditText = view.findViewById(R.id.QREditText)
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OutstandingRentalSheetAdapter(emptyList()) { outstandingRentalSheet ->
            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        sheetSearchBtn.setOnClickListener {
            if (selectedCategory == "리더명") filterByLeader(adapter, outStandingRentalSheetList, searchSheetEdit.text.toString())
            if (selectedCategory == "작업자명") filterByWorker(adapter, outStandingRentalSheetList, searchSheetEdit.text.toString())
            if (selectedCategory == "공기구명") filterByToolName(adapter, outStandingRentalSheetList, searchSheetEdit.text.toString())

        }
        qrCodeBtn.setOnClickListener {
            if (!qrEditText.isFocused) {
                qrEditText.requestFocus()
            }
        }
        qrEditText.setOnEditorActionListener { _, actionId, event ->
            Log.d("tst","textEditted")
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val tag = qrEditText.text.toString().replace("\n", "")
                bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
                bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_BY_TAG,"{tag:\"${tag}\"}",object:BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        val outstandingRentalSheet: OutstandingRentalSheetDto = gson.fromJson(result, type)
                        val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }
                })

                return@setOnEditorActionListener true
            }
            false
        }

        getOutstandingRentalSheetList()
        qrEditText.requestFocus()
        return view
    }

    fun getOutstandingRentalSheetList() {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX,"{toolboxId:${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                val updatedList: List<OutstandingRentalSheetDto> = gson.fromJson(result, type)
                outStandingRentalSheetList = updatedList

                val dbhelper = DatabaseHelper(requireContext()) // 여기부터 DB에 RentalSheet 저장
                dbhelper.clearRSTable()
                for (sheet in outStandingRentalSheetList) {
                    dbhelper.insertRSData(sheet.rentalSheetDto.id, sheet.rentalSheetDto.workerDto.name, sheet.rentalSheetDto.leaderDto.name, sheet.rentalSheetDto.eventTimestamp)
                }
                requireActivity().runOnUiThread {
                    (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(updatedList)
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun filterByLeader(adapter: OutstandingRentalSheetAdapter, sheets: List<OutstandingRentalSheetDto>, keyword: String) {
        val newList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        for (sheet in sheets) {
            if (keyword in sheet.rentalSheetDto.leaderDto.name) {
                newList.add(sheet)
            }
        }
        adapter.updateList(newList)
    }
    fun filterByWorker(adapter: OutstandingRentalSheetAdapter, sheets: List<OutstandingRentalSheetDto>, keyword: String) {
        val newList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        for (sheet in sheets) {
            if (keyword in sheet.rentalSheetDto.workerDto.name) {
                newList.add(sheet)
            }
        }
        adapter.updateList(newList)
    }
    fun filterByToolName(adapter: OutstandingRentalSheetAdapter, sheets: List<OutstandingRentalSheetDto>, keyword: String) {
        val newList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        for (sheet in sheets) {
            for (tool in sheet.rentalSheetDto.toolList) {
                if (keyword in tool.toolDto.name) {
                    newList.add(sheet)
                }
            }

        }
        adapter.updateList(newList)
    }
}