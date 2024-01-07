package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.liquidskr.listener.RentalRequestSheetReadyByMemberReq
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.SheetState
import java.lang.reflect.Type

class WorkerRentalListFragment(var worker: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var connectBtn: ImageButton
    private lateinit var selfRentalBtn: ImageButton
    private lateinit var searchSheetEdit: EditText
    private lateinit var sheetSearchBtn: ImageButton
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    var rentalRequestSheetList: MutableList<RentalRequestSheetDto> = mutableListOf()

    lateinit var welcomeMessage: TextView
    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    private lateinit var rentalRequestSheetReadyByMemberReq: RentalRequestSheetReadyByMemberReq
    private val rentalRequestSheetRequestListener = object: RentalRequestSheetReadyByMemberReq.Listener {
        override fun onNextPage(pageNum: Int) {
            requestRentalRequestSheetReady(pageNum)
        }

        override fun onLastPageArrived() {

        }

        override fun onError(e: Exception) {

        }
        override fun onRentalRequestSheetListUpdated(sheetList: List<RentalRequestSheetDto>) {
            rentalRequestSheetList.addAll(sheetList)
            requireActivity().runOnUiThread {
                (recyclerView.adapter as RentalRequestSheetAdapter).updateList(rentalRequestSheetList)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental_list, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.connectBtn)
        connectBtn.setOnClickListener{
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }

        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)
        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        val layoutManager = LinearLayoutManager(requireContext())

        val adapter = RentalRequestSheetAdapter(emptyList()) { rentalRequestSheet ->
            if (rentalRequestSheet.status != SheetState.REQUEST) {
                val fragment = WorkerRentalDetailFragment(rentalRequestSheet)
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

        sheetSearchBtn.setOnClickListener {

        }

        recyclerView.layoutManager = layoutManager
        selfRentalBtn.setOnClickListener {
            val lobbyActivity = activity as? LobbyActivity

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            sharedViewModel.worker = MembershipSQLite(0, "", "", "", "", "", "", "", "")
            sharedViewModel.leader = MembershipSQLite(0, "", "", "", "", "", "", "", "")
            sharedViewModel.rentalRequestToolIdList.clear()
            val fragment = WorkerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        getRentalRequestSheetList()
        return view
    }

    fun getRentalRequestSheetList() {
        rentalRequestSheetList.clear()

        var sheetCount = 0
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT,"{membershipId:${sharedViewModel.loginWorker.id}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                try {
                    sheetCount = result.toInt()
                    val totalPage = Math.ceil(sheetCount / 10.0).toInt()
                    rentalRequestSheetReadyByMemberReq = RentalRequestSheetReadyByMemberReq(totalPage, sheetCount, rentalRequestSheetRequestListener)
                    requestRentalRequestSheetReady(0)
                } catch (e: Exception) {
                    Log.d("RentalRequestSheetReady", e.toString())
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun requestRentalRequestSheetReady(pageNum: Int) {
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP,"{\"size\":${10},\"page\":${pageNum},membershipId:${sharedViewModel.loginWorker.id}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                rentalRequestSheetReadyByMemberReq.process(page)
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun filterByLeader(adapter: RentalRequestSheetAdapter, sheets: List<RentalRequestSheetDto>, keyword: String) {
        val newList: MutableList<RentalRequestSheetDto> = mutableListOf()
        for (sheet in sheets) {
            if (keyword in sheet.leaderDto.name) {
                newList.add(sheet)
            }
        }
        adapter.updateList(newList)
    }
}