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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.StandByAdapter
import com.mrsmart.standard.membership.MembershipDto

class ManagerStandByFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var standbySyncBtn: ImageButton
    val gson = Gson()
    lateinit var bluetoothManager: BluetoothManager
    private lateinit var connectBtn: ImageButton

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var isPopupVisible = false // // UI블로킹 end

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private lateinit var welcomeMessage: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_standby, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        connectBtn = view.findViewById(R.id.ConnectBtn)
        connectBtn.setOnClickListener{
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        popupLayout = view.findViewById(R.id.popupLayout) // UI블로킹 start
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText) // UI블로킹 end

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.setBluetoothConnectionListener(object : BluetoothManager.BluetoothConnectionListener {
            override fun onBluetoothDisconnected() {
                whenDisconnected()
                Log.d("BluetoothStatus", "Bluetooth 연결이 끊겼습니다.")
            }
        })

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerStandByFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerStandByFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerStandByFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerStandByFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLogin", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        var dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = StandByAdapter(dbHelper.getAllStandby())
        recyclerView.adapter = adapter
        standbySyncBtn = view.findViewById(R.id.standby_SyncBtn)

        standbySyncBtn.setOnClickListener {
            try { // 블루투스가 연결되어있을때만 해야함
                showPopup()
                bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
                bluetoothManager.standbyProcess()
                handler.postDelayed({
                    adapter.updateList(dbHelper.getAllStandby())
                    hidePopup()
                }, 500)
            } catch (e: Exception) {
                handler.post {
                    Toast.makeText(context ,"보류 항목을 전송하려면 블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    fun whenDisconnected () {
        Log.d("bluetooth_","Disconnected3")
        connectBtn.setImageResource(R.drawable.group_11_copy)
        handler.post {
            Toast.makeText(activity, "블루투스 연결이 끊겼습니다. 다시 연결해주세요.", Toast.LENGTH_SHORT).show()
        }
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
}