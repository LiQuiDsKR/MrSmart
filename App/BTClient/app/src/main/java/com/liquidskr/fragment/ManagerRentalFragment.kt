package com.liquidskr.fragment

import SharedViewModel
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetService
import com.mrsmart.standard.toolbox.ToolboxService

class ManagerRentalFragment(val manager: MembershipDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var selfRentalBtn: ImageButton

    private lateinit var rentalBtnField: LinearLayout
    private lateinit var returnBtnField: LinearLayout
    //private lateinit var standbyBtnField: LinearLayout
    private lateinit var registerBtnField: LinearLayout

    private lateinit var welcomeMessage: TextView

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val rentalRequestSheetService = RentalRequestSheetService.getInstance()

    private var bluetoothManager : BluetoothManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_rental, container, false)

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
            fragmentTransform(ManagerRentalDetailFragment(rentalRequestSheet), null)
        }

        rentalBtnField.setOnClickListener {
            fragmentTransform(ManagerRentalFragment(manager), "ManagerRentalFragment")
        }

        returnBtnField.setOnClickListener {
            fragmentTransform(ManagerReturnFragment(manager), "ManagerRentalFragment")
        }

//        standbyBtnField.setOnClickListener {
//            fragmentTransform(ManagerStandByFragment(manager), "ManagerRentalFragment")
//        }

        registerBtnField.setOnClickListener {
            fragmentTransform(ToolRegisterFragment(manager), "ManagerRentalFragment")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter
        rentalRequestSheetService.setAdapter(adapter)
        recyclerView.layoutManager = layoutManager

        selfRentalBtn.setOnClickListener {
            sharedViewModel.worker = null
            sharedViewModel.leader = null
            sharedViewModel.rentalRequestToolIdList.clear()
            fragmentTransform(ManagerSelfRentalFragment(), null)
        }

        return view
    }
    private fun fragmentTransform(frag: Fragment, backStackTag: String?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, frag)
            .addToBackStack(backStackTag)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        bluetoothManager = BluetoothManager.getInstance()

        val toolboxService = ToolboxService.getInstance()
        val toolbox = toolboxService.getToolbox()

        val type =Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT
        val data ="{toolboxId:${toolbox.id}}"
        bluetoothManager?.send(type,data)
    }

    override fun onPause() {
        super.onPause()
        bluetoothManager=null
    }
}