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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto

class ManagerLobbyFragment(manager: MembershipDto) : Fragment(), BluetoothManager.BluetoothConnectionListener {
    lateinit var connectBtn: ImageButton
    lateinit var rentalBtn: ImageButton
    lateinit var returnBtn: ImageButton
    lateinit var standbyBtn: ImageButton
    lateinit var registerBtn: ImageButton
    val manager = manager
    val gson = Gson()
    lateinit var bluetoothManager: BluetoothManager

    lateinit var welcomeMessage: TextView

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_lobby, container, false)
        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        connectBtn = view.findViewById(R.id.ConnectBtn)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        standbyBtn = view.findViewById(R.id.StandbyBtn)
        registerBtn = view.findViewById(R.id.RegisterBtn)
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())

        welcomeMessage.text = manager.name + "님 환영합니다."

        printFragmentStack()

        rentalBtn.setOnClickListener {
            rentalBtn.setImageResource(R.drawable.ic_menu_on_01)
            returnBtn.setImageResource(R.drawable.ic_menu_off_02)
            standbyBtn.setImageResource(R.drawable.ic_menu_off_03)
            registerBtn.setImageResource(R.drawable.ic_menu_off_04)
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
        }

        returnBtn.setOnClickListener {
            rentalBtn.setImageResource(R.drawable.ic_menu_off_01)
            returnBtn.setImageResource(R.drawable.ic_menu_on_02)
            standbyBtn.setImageResource(R.drawable.ic_menu_off_03)
            registerBtn.setImageResource(R.drawable.ic_menu_off_04)
            val lobbyActivity = activity as? LobbyActivity

            val fragment = ManagerReturnFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
        }

        connectBtn.setOnClickListener{
            bluetoothManager.bluetoothOpen()
            bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        }

        standbyBtn.setOnClickListener {
            rentalBtn.setImageResource(R.drawable.ic_menu_off_01)
            returnBtn.setImageResource(R.drawable.ic_menu_off_02)
            standbyBtn.setImageResource(R.drawable.ic_menu_on_03)
            registerBtn.setImageResource(R.drawable.ic_menu_off_04)
            val fragment = ManagerStandByFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
        }

        registerBtn.setOnClickListener {
            val dbHelper = DatabaseHelper(requireContext())
            dbHelper.clearStandbyTable()

            rentalBtn.setImageResource(R.drawable.ic_menu_off_01)
            returnBtn.setImageResource(R.drawable.ic_menu_off_02)
            standbyBtn.setImageResource(R.drawable.ic_menu_off_03)
            registerBtn.setImageResource(R.drawable.ic_menu_on_04)
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, "ToolRegisterFragment")
                .commit()
        }
/*
        override fun onConnectionStateChanged(newState: Int) {
            // 연결 상태가 변경될 때 호출되는 코드
            // newState에는 BluetoothProfile.STATE_CONNECTED 또는 BluetoothProfile.STATE_DISCONNECTED가 전달됩니다.
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Bluetooth 연결이 성공한 경우 처리
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Bluetooth 연결이 끊어진 경우 처리
            }
        }*/

        return view
    }

    override fun onBluetoothDisconnected() {
        activity?.runOnUiThread {
            connectBtn.setImageResource(R.drawable.group_11_copy)
        }
    }
    fun printFragmentStack() {
        val activity = requireActivity()
        val fragmentManager = activity.supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackCount) {
            val fragmentName = fragmentManager.getBackStackEntryAt(i).javaClass.simpleName
            Log.d("FragmentStack", "position : ${fragmentName}")
        }
    }
}