package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetDto
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetService
import com.mrsmart.standard.tag.TagService
import com.mrsmart.standard.toolbox.ToolboxService
import java.lang.NullPointerException

class ManagerReturnFragment() : Fragment(), InputHandler {
    private lateinit var recyclerView: RecyclerView
    //private lateinit var searchSheetEdit: EditText
    //private lateinit var sheetSearchBtn: ImageButton // 이거 없애냐 마냐 ㅋㅋ

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    //lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private val loggedInMembership = MembershipService.getInstance().loggedInMembership
    private lateinit var welcomeMessage: TextView

    private val outstandingRentalSheetService = OutstandingRentalSheetService.getInstance()

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        if (loggedInMembership == null)
            DialogUtils.showAlertDialog("비정상적인 접근", "로그인 정보가 없습니다. 앱을 종료합니다."){ _, _ ->
                requireActivity().finish()
            }
        val manager = loggedInMembership ?: throw NullPointerException("로그인 정보가 없습니다.")

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        //standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        //searchSheetEdit = view.findViewById(R.id.searchSheetEdit)
        //sheetSearchBtn = view.findViewById(R.id.sheetSearchBtn)
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OutstandingRentalSheetAdapter(emptyList<OutstandingRentalSheetDto>().toMutableList()) { outstandingRentalSheet ->
            outstandingRentalSheetService.currentSheetId = outstandingRentalSheet.id
            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                //.replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
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
                .addToBackStack("ManagerReturnFragment")
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
//                .addToBackStack("ManagerReturnFragment")
//                .commit()
//        }

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ManagerReturnFragment")
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        recyclerView.adapter = adapter
        outstandingRentalSheetService.setAdapter(adapter)

        /*
        //검색
        sheetSearchBtn.setOnClickListener {
            val dbHelper = DatabaseHelper.getInstance()
            val name = searchSheetEdit.text.toString()
            try {
                val id = dbHelper.getMembershipIdByName(name)
                getOutstandingRentalSheetListByMembership(id)
            } catch (e: Exception) {
                filterByName(adapter, outStandingRentalSheetList, searchSheetEdit.text.toString())
            }
        }
        */

        return view
    }

/*
    //검색
    private fun filterByName(adapter: OutstandingRentalSheetAdapter, originSheetList: MutableList<OutstandingRentalSheetDto>, keyword: String) {
        val sheetList = originSheetList
        var newSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        for (sheet in sheetList) {
            if ((keyword in sheet.rentalSheetDto.workerDto.name) or (keyword in sheet.rentalSheetDto.leaderDto.name)) {
                newSheetList.add(sheet)
            }
        }
        try {
            adapter.updateList(newSheetList)
        } catch(e: Exception) {
            handler.post {
                Toast.makeText(activity, "검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
 */

    override fun handleInput(input: String) {
        val type = Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_BY_TAG
        val data = "{\"tag\":\"${input}\"}"
        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
    }

    override fun handleTagResponse(response: Any) {
        if (response is OutstandingRentalSheetDto){
            val fragment = ManagerOutstandingDetailFragment(response)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun handleToolboxToolLabelResponse(response: Any) {}

    override fun onResume() {
        super.onResume()
        TagService.getInstance().inputHandler=this
    }

    override fun onDetach() {
        super.onDetach()
        TagService.getInstance().inputHandler=null
    }
}