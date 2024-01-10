package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Data
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
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.liquidskr.listener.OutstandingRentalSheetByMemberReq
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import java.lang.reflect.Type

class ManagerReturnFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var searchSheetEdit: EditText
    private lateinit var sheetSearchBtn: ImageButton
    private lateinit var qrEditText: EditText
    private lateinit var connectBtn: ImageButton

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private lateinit var mContext: Context


    private val handler = Handler(Looper.getMainLooper()) { true } // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end
    private val REQUEST_PAGE_SIZE = 2

    private lateinit var welcomeMessage: TextView

    var outStandingRentalSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
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
            hidePopup()
        }

        override fun onError(e: Exception) {

        }
        override fun onOutstandingRentalSheetUpdated(sheetList: List<OutstandingRentalSheetDto>) {
            outStandingRentalSheetList.addAll(sheetList)
            handler.post {
                (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(outStandingRentalSheetList)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

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

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        qrEditText = view.findViewById(R.id.QREditText)

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end


        recyclerView.layoutManager = LinearLayoutManager(mContext)
        val adapter = OutstandingRentalSheetAdapter(emptyList()) { outstandingRentalSheet ->
            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter

        qrEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val qrcode = qrEditText.text.toString().replace("\n", "")
                qrEditText.text.clear()
                bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_BY_TAG, "{tag:\"${qrcode}\"}", object: BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        try {
                            val outstandingRentalSheet: OutstandingRentalSheetDto = gson.fromJson(result, type)

                            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, fragment)
                                .addToBackStack(null)
                                .commit()
                        } catch (e: Exception) {
                            handler.post {
                                Toast.makeText(activity, "알 수 없는 오류로 인해 반납 전표를 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        handler.post {
                            Toast.makeText(activity, "해당 태그가 포함된 반납 전표를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                return@setOnEditorActionListener true
            }
            false
        }
        sheetSearchBtn.setOnClickListener {
            val dbHelper = DatabaseHelper(mContext)
            val name = searchSheetEdit.text.toString()
            val id = dbHelper.getMembershipIdByName(name)
            getOutstandingRentalSheetListByMembership(id)
            searchSheetEdit.clearFocus()
            qrEditText.requestFocus()
        }

        searchSheetEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                qrEditText.requestFocus()
            }
        }

        getOutstandingRentalSheetList()

        qrEditText.requestFocus()
        return view
    }

    fun getOutstandingRentalSheetListByMembership(id: Long) {
        outStandingRentalSheetList.clear()
        var sheetCount = 0
        try {
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT,"{membershipId:${id}}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    try {
                        sheetCount = result.toInt()
                        val totalPage = Math.ceil(sheetCount/REQUEST_PAGE_SIZE.toDouble()).toInt()
                        outstandingRentalSheetByMemberReq = OutstandingRentalSheetByMemberReq(totalPage, sheetCount, outstandingRentalSheetRequestListener)
                        if (sheetCount>0){
                            requestOutstandingRentalSheetByMembership(0, id)
                        } else{
                            hidePopup()
                        }
                        progressBar.max=totalPage
                    } catch (e: Exception) {
                        Log.d("RentalRequestSheetReady", e.toString())
                    }
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
            showPopup()
        } catch (e: Exception) {
            handler.post {
                Toast.makeText(activity, "목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun requestOutstandingRentalSheetByMembership(pageNum: Int, id: Long) {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP ,"{\"size\":${10},\"page\":${pageNum},membershipId:${id}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                outstandingRentalSheetByMemberReq.process(page)
                handler.post { // UI블로킹 start
                    progressBar.progress = page.pageable.page
                    if ((page.total/REQUEST_PAGE_SIZE) > 0) {
                        progressText.setText("반납 신청 목록 불러오는 중, ${page.pageable.page}/${page.total/REQUEST_PAGE_SIZE}, ${page.pageable.page * 100 / (page.total/REQUEST_PAGE_SIZE)}%")
                    }
                } // UI블로킹 end
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun getOutstandingRentalSheetList() {
        outStandingRentalSheetList.clear()
        var sheetCount = 0
        try {
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT,"{toolboxId:${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    try {
                        sheetCount = result.toInt()
                        val totalPage = Math.ceil(sheetCount / 10.0).toInt()
                        outstandingRentalSheetByMemberReq = OutstandingRentalSheetByMemberReq(totalPage, sheetCount, outstandingRentalSheetRequestListener)
                        if (sheetCount > 0){
                            requestOutstandingRentalSheet(0)
                        } else{
                            hidePopup()
                        }
                    } catch (e: Exception) {
                        Log.d("RentalRequestSheetReady", e.toString())
                    }
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
            showPopup()
        } catch (e: Exception) {
            handler.post {
                Toast.makeText(activity, "목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun requestOutstandingRentalSheet(pageNum: Int) {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX ,"{\"size\":${10},\"page\":${pageNum},toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                outstandingRentalSheetByMemberReq.process(page)
                handler.post { // UI블로킹 start
                    progressBar.progress = page.pageable.page
                    if ((page.total/REQUEST_PAGE_SIZE) > 0) {
                        progressText.setText("반납 신청 목록 불러오는 중, ${page.pageable.page}/${page.total/REQUEST_PAGE_SIZE}, ${page.pageable.page * 100 / (page.total/REQUEST_PAGE_SIZE)}%")
                    }
                } // UI블로킹 end
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
                hidePopup()
            }
        })
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
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LobbyActivity) {
            mContext = context
        }
    }
}