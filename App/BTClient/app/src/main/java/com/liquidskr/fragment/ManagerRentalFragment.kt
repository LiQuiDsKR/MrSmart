package com.liquidskr.fragment

import SharedViewModel
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
import java.lang.reflect.Type

class ManagerRentalFragment(val manager: MembershipDto) : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var selfRentalBtn: ImageButton
    lateinit var searchSheetEdit: EditText
    lateinit var sheetSearchBtn: ImageButton
    private lateinit var connectBtn: ImageButton

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end
    private val REQUEST_PAGE_SIZE = 2

    private lateinit var welcomeMessage: TextView

    private lateinit var bluetoothManager: BluetoothManager
    var rentalRequestSheetList: MutableList<RentalRequestSheetDto> = mutableListOf()

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
            hidePopup() // UI블로킹
        }

        override fun onError(e: Exception) {

        }
        override fun onRentalRequestSheetListUpdated(sheetList: List<RentalRequestSheetDto>) {
            rentalRequestSheetList.addAll(sheetList)
            handler.post {
                (recyclerView.adapter as RentalRequestSheetAdapter).updateList(rentalRequestSheetList)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_manager_rental, container, false)
        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)
        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.ConnectBtn)
        connectBtn.setOnClickListener{
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }
        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLogin", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        val layoutManager = LinearLayoutManager(requireContext())

        val adapter = RentalRequestSheetAdapter(emptyList()) { rentalRequestSheet ->
            val fragment = ManagerRentalDetailFragment(rentalRequestSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        sheetSearchBtn.setOnClickListener {
            // 요청으로 처리
        }

        recyclerView.layoutManager = layoutManager
        selfRentalBtn.setOnClickListener {
            sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
            sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
            sharedViewModel.rentalRequestToolIdList.clear()
            val fragment = ManagerSelfRentalFragment()
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
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT,"{toolboxId:${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                try {
                    sheetCount = result.toInt()
                    val totalPage = Math.ceil(sheetCount/REQUEST_PAGE_SIZE.toDouble()).toInt()
                    rentalRequestSheetReadyByMemberReq = RentalRequestSheetReadyByMemberReq(totalPage, sheetCount, rentalRequestSheetRequestListener)
                    if (sheetCount > 0) { // UI블로킹 start
                        requestRentalRequestSheetReady(0) // 알잘딱 넣으세요
                    } else {
                        hidePopup()
                    }
                    progressBar.max = totalPage // UI블로킹 end
                } catch (e: Exception) {
                    Log.d("RentalRequestSheetReady", e.toString())
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
        showPopup() // UI블로킹
    }

    fun requestRentalRequestSheetReady(pageNum: Int) {
        bluetoothManager.requestData(RequestType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX,"{\"size\":${REQUEST_PAGE_SIZE},\"page\":${pageNum},toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                rentalRequestSheetReadyByMemberReq.process(page)
                handler.post { // UI블로킹 start
                    progressBar.progress = page.pageable.page
                    if ((page.total/REQUEST_PAGE_SIZE) > 0) {
                        progressText.setText("대여 신청 목록 불러오는 중, ${page.pageable.page}/${page.total/REQUEST_PAGE_SIZE}, ${page.pageable.page * 100 / (page.total/REQUEST_PAGE_SIZE)}%")
                    }
                } // UI블로킹 end
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
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