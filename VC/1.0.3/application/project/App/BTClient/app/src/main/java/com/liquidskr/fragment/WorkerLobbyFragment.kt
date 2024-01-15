package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto

class WorkerLobbyFragment(var worker: MembershipDto) : Fragment() {
    private lateinit var connectBtn: ImageView
    private lateinit var rentalBtn: ImageView
    private lateinit var returnBtn: ImageButton
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout

    private val handler = Handler(Looper.getMainLooper()) { true }

    lateinit var welcomeMessage: TextView
    val gson = Gson()
    private lateinit var bluetoothManager: BluetoothManager
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_lobby, container, false)

        connectBtn = view.findViewById(R.id.connectBtn)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.setBluetoothConnectionListener(object : BluetoothManager.BluetoothConnectionListener {
            override fun onBluetoothDisconnected() {
                handler.post {
                    connectBtn.setImageResource(R.drawable.group_11_copy)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결이 끊겼습니다.")
            }

            override fun onBluetoothConnected() {
                handler.post {
                    connectBtn.setImageResource(R.drawable.manager_lobby_connectionbtn)
                }
                Log.d("BluetoothStatus", "Bluetooth 연결에 성공했습니다.")
            }
        })

        rentalBtnField.setOnClickListener {
            rentalBtn.setImageResource(R.drawable.ic_menu_on_01)
            returnBtn.setImageResource(R.drawable.ic_menu_off_02)
            val fragment = WorkerRentalListFragment(worker)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            rentalBtn.setImageResource(R.drawable.ic_menu_off_01)
            returnBtn.setImageResource(R.drawable.ic_menu_on_02)

            val fragment = WorkerReturnListFragment(worker)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }

        connectBtn.setOnClickListener{
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            bluetoothManager.bluetoothOpen()
        }

        //getOutstandingRentalSheetList()
        return view
    }
    /*
    fun getOutstandingRentalSheetList() {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP,"{membershipId:${sharedViewModel.loginWorker.id}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                val updatedList: List<OutstandingRentalSheetDto> = gson.fromJson(result, type)
                requireActivity().runOnUiThread {
                    (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(updatedList)
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }*/
}