package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.Membership

class ManagerLobbyFragment(manager: Membership) : Fragment() {
    lateinit var rentalBtn: ImageButton
    lateinit var returnBtn: ImageButton
    lateinit var standbyBtn: ImageButton
    lateinit var registerBtn: ImageButton
    val manager = manager;
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
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        standbyBtn = view.findViewById(R.id.StandbyBtn)
        registerBtn = view.findViewById(R.id.RegisterBtn)
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())

        welcomeMessage.text = manager.name + "님 환영합니다."


        rentalBtn.setOnClickListener {
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                //.addToBackStack(null)
                .commit()
        }

        returnBtn.setOnClickListener {
            // LobbyActivity에서 만든 managerRentalFragment를 가져와서 사용
            val lobbyActivity = activity as? LobbyActivity
            val managerReturnFragment = lobbyActivity?.managerReturnFragment

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            managerReturnFragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, it)
                    .commit()
            }
        }

        standbyBtn.setOnClickListener {
            val fragment = ManagerStandByFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }

        registerBtn.setOnClickListener {
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
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
}