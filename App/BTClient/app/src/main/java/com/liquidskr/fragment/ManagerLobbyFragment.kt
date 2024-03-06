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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto

class ManagerLobbyFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var rentalBtn: ImageButton
    private lateinit var returnBtn: ImageButton
    //private lateinit var standbyBtn: ImageButton
    private lateinit var registerBtn: ImageButton
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    //private lateinit var standbyBtnField: LinearLayout
    private lateinit var registerBtnField: LinearLayout

    private lateinit var popupLayout: View

    val gson = Gson()

    private lateinit var welcomeMessage: TextView

    // not using in this Fragment
    private var bluetoothManager : BluetoothManager? = null

    private val bluetoothManagerListener = object : BluetoothManager.Listener{
        override fun onDisconnected() {
            val reconnectFrag = ReconnectFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.popupLayout,reconnectFrag)
                .addToBackStack(null)
                .commit()
        }

        override fun onRequestStarted() {
            //TODO("Not yet implemented")
            val progressBarFrag = ProgressBarFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.popupLayout,progressBarFrag)
                .addToBackStack(null)
                .commit()

            // 접근 불가.
            Log.d("bluetooth","Inaccessible point! : ${this::class.java}, onRequestStarted")
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            // 접근 불가.
            Log.d("bluetooth","Inaccessible point! : ${this::class.java}, onRequestProcessed")
        }

        override fun onRequestEnded() {
            // 접근 불가.
            Log.d("bluetooth","Inaccessible point! : ${this::class.java}, onRequestEnded")
        }

        override fun onRequestFailed(message: String) {
            // 접근 불가.
            Log.d("bluetooth","Inaccessible point! : ${this::class.java}, onRequestFailed")
        }

        override fun onException(message: String) {
            Log.d("bluetooth","Exception : ${this::class.java}, ${message}")
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_lobby, container, false)
        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        //standbyBtn = view.findViewById(R.id.StandbyBtn)
        registerBtn = view.findViewById(R.id.RegisterBtn)

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        //standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        popupLayout = view.findViewById(R.id.popupLayout)
        welcomeMessage.text = manager.name + "님 환영합니다."

        (requireActivity() as MainActivity).setBluetoothManagerListener(bluetoothManagerListener)

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

//        standbyBtnField.setOnClickListener {
//            val fragment = ManagerStandByFragment(manager)
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .addToBackStack("ManagerLobbyFragment")
//                .commit()
//        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        return view
    }
    override fun onResume() {
        super.onResume()
        bluetoothManager = (requireActivity() as MainActivity).bluetoothManager
    }

    override fun onPause() {
        super.onPause()
        bluetoothManager=null
    }
}