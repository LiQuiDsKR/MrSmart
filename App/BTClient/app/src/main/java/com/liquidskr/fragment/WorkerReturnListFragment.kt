package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetDto
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetService
import com.mrsmart.standard.sheet.outstanding.OutstandingState
import java.lang.NullPointerException

class WorkerReturnListFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var selfRentalBtnField: LinearLayout
    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

    private val outstandingRentalSheetService = OutstandingRentalSheetService.getInstance()

    private val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_return_list, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val worker = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = worker.name + "님 환영합니다."

        selfRentalBtnField = view.findViewById(R.id.SelfRentalBtnField)
        rentalBtnField  = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)

        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OutstandingRentalSheetAdapter(emptyList<OutstandingRentalSheetDto>().toMutableList()) { outstandingRentalSheet ->
            if (outstandingRentalSheet.outstandingStatus != OutstandingState.REQUEST) {
                outstandingRentalSheetService.currentSheetId = outstandingRentalSheet.id
                val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        selfRentalBtnField.setOnClickListener {
            val fragment = WorkerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerReturnFragment")
                .commit()
        }
        rentalBtnField.setOnClickListener {
            val fragment = WorkerRentalListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerReturnFragment")
                .commit()

            val type = Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT
            val data = "{membershipId:${worker.id}}"
            bluetoothManager?.send(type,data)
        }

        returnBtnField.setOnClickListener {
            val fragment = WorkerReturnListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("WorkerReturnFragment")
                .commit()

            val type = Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT
            val data = "{membershipId:${worker.id}}"
            bluetoothManager?.send(type,data)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("WorkerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter
        outstandingRentalSheetService.setAdapter(adapter)

        return view
    }
}