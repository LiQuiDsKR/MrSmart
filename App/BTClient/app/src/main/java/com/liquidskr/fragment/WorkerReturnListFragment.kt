package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.listener.OutstandingRentalSheetByMemberReq
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetDto
import com.mrsmart.standard.sheet.outstanding.OutstandingState
import java.lang.NullPointerException
import java.lang.reflect.Type

class WorkerReturnListFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var connectBtn: ImageButton
    private lateinit var bluetoothManagerOld: BluetoothManager_Old
    private lateinit var searchSheetEdit: EditText
    private lateinit var sheetSearchBtn: ImageButton
    private lateinit var qrCodeBtn: LinearLayout
    private lateinit var qrEditText: EditText
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end
    private val REQUEST_PAGE_SIZE = 2

    var outStandingRentalSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView
    val gson = Gson()

    private lateinit var outstandingRentalSheetByMemberReq: OutstandingRentalSheetByMemberReq
    private val outstandingRentalSheetRequestListener = object: OutstandingRentalSheetByMemberReq.Listener {
        override fun onNextPage(pageNum: Int) {

        }

        override fun onLastPageArrived() {
            hidePopup() // UI블로킹
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

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val worker = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.connectBtn)

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        connectBtn.setOnClickListener{
            bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()
            bluetoothManagerOld.bluetoothOpen()
        }
        bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()
        bluetoothManagerOld.setBluetoothConnectionListener(object : BluetoothManager_Old.BluetoothConnectionListener {
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
        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        qrEditText = view.findViewById(R.id.QREditText)
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OutstandingRentalSheetAdapter(emptyList<OutstandingRentalSheetDto>().toMutableList()) { outstandingRentalSheet ->
            if (outstandingRentalSheet.outstandingStatus != OutstandingState.REQUEST) {
                val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        sheetSearchBtn.setOnClickListener {
            filterByName(adapter, outStandingRentalSheetList, searchSheetEdit.text.toString())
        }

        rentalBtnField.setOnClickListener {
            val fragment = WorkerRentalListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {

            val fragment = WorkerReturnListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("WorkerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter
        return view
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
    private fun filterByName(adapter: OutstandingRentalSheetAdapter, originSheetList: MutableList<OutstandingRentalSheetDto>, keyword: String) {
        val sheetList = originSheetList
        var newSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        for (sheet in sheetList) {
            if ((keyword in sheet.rentalSheetDto.workerDto.name) or (keyword in sheet.rentalSheetDto.leaderDto.name)) {
                newSheetList.add(sheet)
            }
        }
        try {
            adapter.updateList(newSheetList)
        } catch(e: Exception) {
            handler.post {
                Toast.makeText(activity, "검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // ## 여기서부터 블루투스 송수신 시 UI블로킹 start
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
    // UI블로킹 end
}