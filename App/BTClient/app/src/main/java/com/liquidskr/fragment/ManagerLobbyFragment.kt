package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.toolbox.ToolboxService
import java.lang.NullPointerException

class ManagerLobbyFragment() : Fragment() {
    private lateinit var selfRentalBtnField: LinearLayout
    private lateinit var rentalBtn: ImageButton
    private lateinit var returnBtn: ImageButton
    //private lateinit var standbyBtn: ImageButton
    private lateinit var registerBtn: ImageButton
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    //private lateinit var standbyBtnField: LinearLayout
    private lateinit var registerBtnField: LinearLayout

    private lateinit var popupLayout: View

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

    private var bluetoothManager : BluetoothManager? =null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_lobby, container, false)
        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        //standbyBtn = view.findViewById(R.id.StandbyBtn)
        registerBtn = view.findViewById(R.id.RegisterBtn)

        selfRentalBtnField = view.findViewById(R.id.SelfRentalBtnField)
        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        //standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        popupLayout = view.findViewById(R.id.popupLayout)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val manager = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage.text = manager.name + "님 환영합니다."

        selfRentalBtnField.setOnClickListener {
            val fragment = ManagerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }
        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
            val toolboxService = ToolboxService.getInstance()
            val toolbox = toolboxService.getToolbox()

            val type =Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT
            val data ="{toolboxId:${toolbox.id}}"
            bluetoothManager?.send(type,data)

        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
            val toolboxService = ToolboxService.getInstance()
            val toolbox = toolboxService.getToolbox()

            val type =Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT
            val data ="{toolboxId:${toolbox.id}}"
            bluetoothManager?.send(type,data)
        }

//        standbyBtnField.setOnClickListener {
//            val fragment = ManagerStandByFragment(manager)
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .addToBackStack("ManagerLobbyFragment")
//                .commit()
//        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        bluetoothManager = BluetoothManager.getInstance()
    }

    override fun onPause() {
        super.onPause()
        bluetoothManager = null
    }
}