package com.liquidskr.fragment

import SharedViewModel
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolRegisterAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tag.TagService
import com.mrsmart.standard.tag.ToolboxToolLabelService
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolService
import com.mrsmart.standard.toolbox.ToolboxService
import kotlinx.coroutines.selects.select


class ToolRegisterFragment(val manager: MembershipDto) : Fragment(), InputHandler {
    private lateinit var recyclerView: RecyclerView

    lateinit var rentalBtnField: LinearLayout
    lateinit var returnBtnField: LinearLayout
    //lateinit var standbyBtnField: LinearLayout
    lateinit var registerBtnField: LinearLayout

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private lateinit var welcomeMessage: TextView

    private var selectedTag : String? = null
    private var selectedToolId : Long = 0

    private val toolService = ToolService.getInstance()
    private val toolboxService = ToolboxService.getInstance()
    private val toolboxToolLabelService = ToolboxToolLabelService.getInstance()
    private val tagService = TagService.getInstance()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        welcomeMessage.text = manager.name + "님 환영합니다."

        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rentalBtnField = view.findViewById(R.id.RentalBtnField)
        returnBtnField = view.findViewById(R.id.ReturnBtnField)
        //standbyBtnField = view.findViewById(R.id.StandbyBtnField)
        registerBtnField = view.findViewById(R.id.RegisterBtnField)

        val tools: List<ToolDto> = toolService.getAllTools()

        val adapter = ToolRegisterAdapter(tools){
            selectedTag=null
            selectedToolId = it.id
            val type = Constants.BluetoothMessageType.TAG_LIST_BY_TOOL_AND_TOOLBOX_ID
            val data = "{\"toolId\":${it.id},\"toolboxId\":${toolboxService.getToolbox().id}}"
            bluetoothManager?.send(type,data)
        }

        rentalBtnField.setOnClickListener {
            val fragment = ManagerRentalFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
            val toolboxService = ToolboxService.getInstance()
            val toolbox = toolboxService.getToolbox()

            //val type =Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT
            //val data ="{toolboxId:${toolbox.id}}"
            //bluetoothManager?.send(type,data)
        }

        returnBtnField.setOnClickListener {
            val fragment = ManagerReturnFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }

        /*
        standbyBtnField.setOnClickListener {
            val fragment = ManagerStandByFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }
        */

        registerBtnField.setOnClickListener {
            val fragment = ToolRegisterFragment(manager)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack("ManagerLobbyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        searchBtn.setOnClickListener {
            val name = editTextName.text.toString()
            val list = toolService.searchToolByName(name)
            adapter.updateList(list)
        }
        editTextName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                // 키보드 숨기기
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                searchBtn.performClick()
                editTextName.clearFocus()
                true
            }else{
                false
            }
        }

        recyclerView.adapter = adapter

        return view
    }

    override fun handleInput(input: String) {
        if (toolboxToolLabelService.isToolboxToolLabelExist(input)) {
            selectedTag=null
            selectedToolId= toolService.getToolByTBT(input).id
            val type = Constants.BluetoothMessageType.TAG_LIST_BY_TOOLBOX_TOOL_LABEL_QRCODE
            val data = "{\"toolboxToolLabel\":\"$input\"}"
            bluetoothManager?.send(type,data)
        }else{
            selectedTag=input
            val type = Constants.BluetoothMessageType.TAG_LIST_BY_TAG_MACADDRESS
            val data = "{\"tag\":\"$input\"}"
            bluetoothManager?.send(type,data)
        }
    }

    override fun handleTagResponse(response: Any) {
        if (response is List<*> && response.isNotEmpty() && response[0] is TagDto) {
            val tags = response as List<TagDto>
            val tag = tags[0]
            val tool = toolService.getToolById(tag.toolDto.id)
            val fragment = ToolRegisterDetailFragment(tool, tags.map{it.macaddress},selectedTag)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }else if (response is List<*> && response.isEmpty()){
            val tool = toolService.getToolById(selectedToolId)
            val fragment = ToolRegisterDetailFragment(tool, emptyList(),selectedTag)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("ToolRegisterFragment")
                .commit()
        }
    }

    override fun handleToolboxToolLabelResponse(response: Any) {}

    override fun onResume() {
        super.onResume()
        tagService.inputHandler=this
        toolboxToolLabelService.inputHandler=this
    }

    override fun onDetach() {
        super.onDetach()
        tagService.inputHandler=null
        toolboxToolLabelService.inputHandler=null
    }
}