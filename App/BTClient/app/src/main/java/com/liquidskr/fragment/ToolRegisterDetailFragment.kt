package com.liquidskr.fragment

import SharedViewModel
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolRegisterTagDetailAdapter
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tag.TagService
import com.mrsmart.standard.tag.ToolboxToolLabelDto
import com.mrsmart.standard.tag.ToolboxToolLabelService
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.toolbox.ToolboxService
import kotlinx.coroutines.selects.select

class ToolRegisterDetailFragment(var tool: ToolDto, var tagList: List<String>, var selectedTag:String?) : Fragment(), InputHandler {
    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView
    private lateinit var context: Context

    private lateinit var check_tag: ImageButton
    private lateinit var check_label: ImageButton

    private lateinit var qrDisplay: TextView
    private var tbtQrcode: String = ""

    lateinit var recyclerView: RecyclerView

    private lateinit var backButton: ImageButton
    private lateinit var confirmBtn: LinearLayout

    private val toolboxToolLabelService = ToolboxToolLabelService.getInstance()
    private val tagService = TagService.getInstance()
    private val toolboxService = ToolboxService.getInstance()

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    var labelFlag = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register_detail, container, false)
        view.requestFocus()

        val handler = Handler(Looper.getMainLooper())
        context = requireContext()
        toolName = view.findViewById(R.id.Register_ToolName)
        toolSpec = view.findViewById(R.id.Register_ToolSpec)

        check_label = view.findViewById(R.id.check_label)
        check_tag = view.findViewById(R.id.check_tag)

        qrDisplay = view.findViewById(R.id.qr_display)

        toolName.text = tool.name
        toolSpec.text = tool.spec

        recyclerView = view.findViewById(R.id.qrRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        var adapter = ToolRegisterTagDetailAdapter(tagList.toMutableList())

        backButton = view.findViewById(R.id.backButton)

        confirmBtn = view.findViewById(R.id.confirmBtn)

        qrDisplay.text = "미등록"
        try {
            val dbHepler = DatabaseHelper.getInstance()
            val label = dbHepler.getTBTByToolId(tool.id)
            qrDisplay.text = label
            tbtQrcode = label
        } catch(e: Exception) {
            e.printStackTrace()
            handler.post {
                Toast.makeText(context, "공기구의 라벨 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        check_label.setOnClickListener {
            labelFlag = true
            check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_on)
            check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
        }
        check_tag.setOnClickListener {
            labelFlag = false
            check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
            check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_on)
        }

        confirmBtn.setOnClickListener {
            if (tbtQrcode == "") {
                DialogUtils.showAlertDialog("QR 정보 없음", "선반 QR 정보가 없습니다.")
            } else {
                DialogUtils.showAlertDialog("QR 정보 저장", "저장하시겠습니까?"){_,_->
                    confirm()
                }
            }
        }

        recyclerView.adapter = adapter

        if (selectedTag != null) {
            (recyclerView.adapter as ToolRegisterTagDetailAdapter).qrCheck(selectedTag!!)
        }

        return view
    }

    fun confirm() {
        val tagList = (recyclerView.adapter as ToolRegisterTagDetailAdapter).getResult()
        val tagString = gson.toJson(tagList)
        val type = Constants.BluetoothMessageType.TAG_AND_TOOLBOX_TOOL_LABEL_FORM
        val data = "{\"toolId\":${tool.id},\"tagList\":$tagString,\"toolboxToolLabel\":\"$tbtQrcode\",\"toolboxId\":${toolboxService.getToolbox().id},\"qrcode\":\"${tbtQrcode}\"}"
        bluetoothManager.send(type, data)
    }

    override fun handleInput(input: String) {
        if (labelFlag) {
            val type = Constants.BluetoothMessageType.TOOLBOX_TOOL_LABEL_AVAILABLE
            val data = "{\"toolboxToolLabel\":\"$input\"}"
            bluetoothManager.send(type,data)
        } else {
            val flag = !(recyclerView.adapter as ToolRegisterTagDetailAdapter).qrCheck(input)
            if (flag) {
                val type = Constants.BluetoothMessageType.TAG_AVAILABLE
                val data = "{\"tag\":\"$input\"}"
                bluetoothManager.send(type, data)
            }
        }
    }

    override fun handleTagResponse(response: Any) {
        if (response is String && response.get(0)!='0'){
            val tag = response.substring(1)
            (recyclerView.adapter as ToolRegisterTagDetailAdapter).addTag(tag)
        }else{
            Toast.makeText(activity, "이미 사용 중인 QR코드입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun handleToolboxToolLabelResponse(response: Any) {
        if (response is String && response.get(0)!='0'){
            tbtQrcode = response.substring(1)
            qrDisplay.text = tbtQrcode
        }else{
            Toast.makeText(activity, "이미 사용 중인 QR코드입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        tagService.inputHandler = this
        toolboxToolLabelService.inputHandler= this
    }

    override fun onDetach() {
        super.onDetach()
        tagService.inputHandler= null
        toolboxToolLabelService.inputHandler= null
    }
}