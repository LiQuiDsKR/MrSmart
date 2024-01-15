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

    private val handler = Handler(Looper.getMainLooper()) { true } // UI블로킹 start
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
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
            try {
                bluetoothManager.bluetoothOpen()
                connectBtn.setImageResource(R.drawable.manager_lobby_connectionbtn)
            } catch (e: Exception) {
                Toast.makeText(context, "연결에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
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
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        var dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = StandByAdapter(dbHelper.getAllStandby())
        recyclerView.adapter = adapter
        standbySyncBtn = view.findViewById(R.id.standby_SyncBtn)

        standbySyncBtn.setOnClickListener {
            showPopup()
            try {
                bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
                if (adapter.sheets.size > 0) {
                    if (bluetoothManager.isConnected) {
                        bluetoothManager.standbyProcess()
                        handler.postDelayed({
                            adapter.updateList(dbHelper.getAllStandby())
                        }, 500)
                        if (adapter.sheets.isEmpty()) {
                            handler.post {
                                Toast.makeText(context ,"모든 보류 항목을 처리했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        hidePopup()
                    } else {
                        handler.post {
                            Toast.makeText(context ,"보류 항목을 전송하려면 블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    handler.post {
                        Toast.makeText(context ,"보류 항목이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                handler.post {
                    Toast.makeText(context ,"보류 항목을 전송하지 못했습니다.", Toast.LENGTH_SHORT).show()
                    hidePopup()
                }
            }

        }
        return view
    }

    fun whenDisconnected () {
        handler.post {
            hidePopup()
            connectBtn.setImageResource(R.drawable.group_11_copy)
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