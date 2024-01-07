package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto

class ManagerLobbyFragment(val manager: MembershipDto) : Fragment(), BluetoothManager.BluetoothConnectionListener {
    lateinit var connectBtn: ImageButton
    lateinit var rentalBtn: ImageButton
    lateinit var returnBtn: ImageButton
    lateinit var standbyBtn: ImageButton
    lateinit var registerBtn: ImageButton

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout
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
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }
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

    override fun onBluetoothDisconnected() {
        activity?.runOnUiThread {
            connectBtn.setImageResource(R.drawable.group_11_copy)
        }
    }
}