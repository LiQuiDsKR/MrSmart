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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto

class ManagerLobbyFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var connectBtn: ImageButton
    private lateinit var rentalBtn: ImageButton
    private lateinit var returnBtn: ImageButton
    private lateinit var standbyBtn: ImageButton
    private lateinit var registerBtn: ImageButton

    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    private lateinit var standbyBtnField: LinearLayout
    private lateinit var registerBtnField: LinearLayout

    private val handler = Handler(Looper.getMainLooper()) { true }
    val gson = Gson()
    lateinit var bluetoothManager: BluetoothManager

    private lateinit var welcomeMessage: TextView

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_lobby, container, false)
        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        standbyBtn = view.findViewById(R.id.StandbyBtn)
        registerBtn = view.findViewById(R.id.RegisterBtn)

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())

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
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        return view
    }
}