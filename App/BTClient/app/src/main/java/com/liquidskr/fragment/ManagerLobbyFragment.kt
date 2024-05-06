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
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import java.lang.NullPointerException

class ManagerLobbyFragment() : Fragment() {
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

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

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

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val manager = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage.text = manager.name + "님 환영합니다."

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment()
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
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerLobbyFragment")
                .commit()
        }

        return view
    }
}