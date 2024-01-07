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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.listener.OutstandingRentalSheetByMemberReq
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.OutstandingState
import java.lang.reflect.Type

class WorkerReturnListFragment(var worker: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var connectBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var searchSheetEdit: EditText
    private lateinit var sheetSearchBtn: ImageButton
    private lateinit var qrCodeBtn: LinearLayout
    private lateinit var qrEditText: EditText
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    var outStandingRentalSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
    lateinit var welcomeMessage: TextView
    val gson = Gson()

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
                (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(outStandingRentalSheetList)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_return_list, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.connectBtn)
        connectBtn.setOnClickListener{
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }

        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        qrCodeBtn = view.findViewById(R.id.QRcodeBtn)
        qrEditText = view.findViewById(R.id.QREditText)
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OutstandingRentalSheetAdapter(emptyList()) { outstandingRentalSheet ->
            if (outstandingRentalSheet.outstandingStatus != OutstandingState.REQUEST) {
                val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        rentalBtnField.setOnClickListener {
            val fragment = WorkerRentalListFragment(worker)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {

            val fragment = WorkerReturnListFragment(worker)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        recyclerView.adapter = adapter

        getOutstandingRentalSheetList()
        return view
    }

    fun getOutstandingRentalSheetList() {
        outStandingRentalSheetList.clear()

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