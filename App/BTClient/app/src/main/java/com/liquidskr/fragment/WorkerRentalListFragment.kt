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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.sheet.rental.SheetState
import java.lang.reflect.Type

class WorkerRentalListFragment(var worker: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var connectBtn: ImageButton
    private lateinit var selfRentalBtn: ImageButton
    private lateinit var searchSheetEdit: EditText
    private lateinit var sheetSearchBtn: ImageButton
    private lateinit var bluetoothManagerOld: BluetoothManager_Old
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // UI블로킹 end
    private val REQUEST_PAGE_SIZE = 2

    var rentalRequestSheetList: MutableList<RentalRequestSheetDto> = mutableListOf()

    lateinit var welcomeMessage: TextView
    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental_list, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.connectBtn)
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

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)
        searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        val layoutManager = LinearLayoutManager(requireContext())

        val adapter = RentalRequestSheetAdapter(emptyList<RentalRequestSheetDto>().toMutableList()) { rentalRequestSheet ->
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
            filterByName(adapter, rentalRequestSheetList, searchSheetEdit.text.toString())
        }

        recyclerView.layoutManager = layoutManager
        selfRentalBtn.setOnClickListener {
            val mainActivity = activity as? MainActivity

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            sharedViewModel.worker = null
            sharedViewModel.leader = null
            sharedViewModel.rentalRequestToolIdList.clear()
            sharedViewModel.toolWithCountList.clear()
            val fragment = WorkerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("WorkerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        getRentalRequestSheetList()
        return view
    }

    fun getRentalRequestSheetList() {
        rentalRequestSheetList.clear()
        showPopup()
        var sheetCount = 0
        bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()
        bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT,"{membershipId:${sharedViewModel.loginWorker!!.id}}",object:BluetoothManager_Old.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                try {
                    sheetCount = result.toInt()
                    val totalPage = Math.ceil(sheetCount / 10.0).toInt()
                    //rentalRequestSheetReadyByMemberReq = RentalRequestSheetReadyByMemberReq(totalPage, sheetCount, rentalRequestSheetRequestListener)
                    handler.post {
                        if (sheetCount > 0) { // UI블로킹 start
                            requestRentalRequestSheetReady(0) // 알잘딱 넣으세요
                        } else {
                            hidePopup()
                        }
                    }
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
        bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP,"{\"size\":${REQUEST_PAGE_SIZE},\"page\":${pageNum},membershipId:${sharedViewModel.loginWorker!!.id}}",object: BluetoothManager_Old.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                var page: Page = gson.fromJson(result, type)
                //rentalRequestSheetReadyByMemberReq.process(page)
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
    private fun filterByName(adapter: RentalRequestSheetAdapter, originSheetList: MutableList<RentalRequestSheetDto>, keyword: String) {
        val sheetList = originSheetList
        var newSheetList: MutableList<RentalRequestSheetDto> = mutableListOf()
        for (sheet in sheetList) {
            if ((keyword in sheet.workerDto.name) or (keyword in sheet.leaderDto.name)) {
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