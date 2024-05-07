package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
import java.lang.NullPointerException

class WorkerLobbyFragment() : Fragment() {
    private lateinit var selfRentalBtnField: LinearLayout
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout

    private lateinit var popupLayout: View

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

    private var bluetoothManager : BluetoothManager? =null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_worker_lobby, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val worker = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        selfRentalBtnField = view.findViewById(R.id.SelfRentalBtnField)
        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        popupLayout = view.findViewById(R.id.popupLayout)
        welcomeMessage.text = worker.name + "님 환영합니다."

        selfRentalBtnField.setOnClickListener {
            val fragment = WorkerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()
        }
        rentalBtnField.setOnClickListener {
            val fragment = WorkerRentalListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()

            val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT
            val data = "{membershipId:${worker.id}}"
            bluetoothManager?.send(type,data)
        }

        returnBtnField.setOnClickListener {
            val fragment = WorkerReturnListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerLobbyFragment")
                .commit()

            val type = Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT
            val data = "{membershipId:${worker.id}}"
            bluetoothManager?.send(type,data)
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