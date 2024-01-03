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
import com.liquidskr.btclient.OutstandingRentalSheetByMemberReq
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.liquidskr.btclient.RentalRequestSheetReadyByMemberReq
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import java.lang.reflect.Type

class WorkerReturnListFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager
    lateinit var searchSheetEdit: EditText
    lateinit var sheetSearchBtn: ImageButton
    lateinit var qrCodeBtn: LinearLayout
    lateinit var qrEditText: EditText
    var outStandingRentalSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
    val gson = Gson()

    interface KeyListener {
        fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    }
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    private lateinit var outstandingRentalSheetByMemberReq: OutstandingRentalSheetByMemberReq
    private val outstandingRentalSheetRequestListener = object: OutstandingRentalSheetByMemberReq.Listener {
        override fun onNextPage(pageNum: Int) {
            requestOutstandingRentalSheet(pageNum)
        }

        override fun onLastPageArrived() {

        }

        override fun onError(e: Exception) {

        }
        override fun onOutstandingRentalSheetUpdated(sheetList: List<OutstandingRentalSheetDto>) {
            outStandingRentalSheetList.addAll(sheetList)
            requireActivity().runOnUiThread {
                (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(sheetList)
            }
        }
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
            val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        sheetSearchBtn.setOnClickListener {

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

        var sheetCount = 0
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT,"{membershipId:${sharedViewModel.loginWorker.id}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                try {
                    sheetCount = result.toInt()
                    val totalPage = Math.ceil(sheetCount / 10.0).toInt()
                    outstandingRentalSheetByMemberReq = OutstandingRentalSheetByMemberReq(totalPage, sheetCount, outstandingRentalSheetRequestListener)
                    requestOutstandingRentalSheet(0)
                } catch (e: Exception) {
                    Log.d("RentalRequestSheetReady", e.toString())
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun requestOutstandingRentalSheet(pageNum: Int) {
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP ,"{\"size\":${10},\"page\":${pageNum},membershipId:${sharedViewModel.loginWorker.id}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                outstandingRentalSheetByMemberReq.process(page)
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
}