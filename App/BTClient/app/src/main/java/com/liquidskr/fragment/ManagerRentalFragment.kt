package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetService
import com.mrsmart.standard.toolbox.ToolboxService
import java.lang.NullPointerException

class ManagerRentalFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var selfRentalBtn: ImageButton

    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    //private lateinit var standbyBtnField: LinearLayout
    private lateinit var registerBtnField: LinearLayout

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

    private val rentalRequestSheetService = RentalRequestSheetService.getInstance()

    private var bluetoothManager : BluetoothManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_rental, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val manager = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")


        recyclerView = view.findViewById(R.id.Manager_Rental_RecyclerView)
        selfRentalBtn = view.findViewById(R.id.Manager_SelfRentalBtn)

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        //standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = RentalRequestSheetAdapter(emptyList<RentalRequestSheetDto>().toMutableList()) { rentalRequestSheet ->
            rentalRequestSheetService.currentSheetId= rentalRequestSheet.id
            val fragment = ManagerRentalDetailFragment(rentalRequestSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
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
                .addToBackStack("ManagerRentalFragment")
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
//                .addToBackStack("ManagerRentalFragment")
//                .commit()
//        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerRentalFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter
        rentalRequestSheetService.setAdapter(adapter)
        recyclerView.layoutManager = layoutManager

        selfRentalBtn.setOnClickListener {
            val fragment = ManagerSelfRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
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
        bluetoothManager=null
    }
}