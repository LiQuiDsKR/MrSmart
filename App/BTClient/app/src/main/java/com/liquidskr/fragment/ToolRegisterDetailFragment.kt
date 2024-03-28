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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.ToolRegisterTagDetailAdapter
import com.mrsmart.standard.tool.ToolDto

class ToolRegisterDetailFragment(var tool: ToolDto, var tagList: List<String>) : Fragment() {


    private lateinit var toolName: TextView
    private lateinit var toolSpec: TextView
    private lateinit var context: Context

    private lateinit var check_tag: ImageButton
    private lateinit var check_label: ImageButton

    private lateinit var qrDisplay: TextView
    var tbtQrcode: String = ""

    lateinit var recyclerView: RecyclerView

    private lateinit var backButton: ImageButton
    private lateinit var confirmBtn: LinearLayout

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tool_register_detail, container, false)

        val handler = Handler(Looper.getMainLooper())
        context = requireContext()
        toolName = view.findViewById(R.id.Register_ToolName)
        toolSpec = view.findViewById(R.id.Register_ToolSpec)

        check_label = view.findViewById(R.id.check_label)
        check_tag = view.findViewById(R.id.check_tag)

        toolName.text = tool.name
        toolSpec.text = tool.spec
        qrDisplay = view.findViewById(R.id.qr_display)

        recyclerView = view.findViewById(R.id.qrRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        var adapter = ToolRegisterTagDetailAdapter(tagList)

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

        var isLabelMode = true
        check_label.setOnClickListener {
            isLabelMode = !isLabelMode
            if (isLabelMode) {
                check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_on)
                check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
            }
        }
        check_tag.setOnClickListener {
            isLabelMode = !isLabelMode
            check_label.setImageResource(R.drawable.icon_choice_ic_choice_round_off)
            check_tag.setImageResource(R.drawable.icon_choice_ic_choice_round_on)

        }

        confirmBtn.setOnClickListener {
            if (tbtQrcode == "") {
                Toast.makeText(context, "선반 코드가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show()
            } else {
                //showBluetoothModal("알림","라벨 정보를 등록하시겠습니까?")
            }
        }

        recyclerView.adapter = adapter

        return view
    }
}