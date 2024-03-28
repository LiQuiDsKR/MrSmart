package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.InputHandler
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestToolAdapter
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.returns.ReturnToolFormSelectedDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.TagService

class ManagerOutstandingDetailFragment(private var outstandingRentalSheet: OutstandingRentalSheetDto) : Fragment(),
    InputHandler {
    private lateinit var recyclerView: RecyclerView

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout

    val gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_return_detail, container, false)

        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton = view.findViewById(R.id.backButton)

        returnerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        workerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingRentalSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingRentalSheet.rentalSheetDto.eventTimestamp

        val adapter = OutstandingDetailAdapter(
            outstandingRentalSheet.rentalSheetDto.toolList.map {
                ReturnToolFormSelectedDto(
                    rentalToolDtoId = it.id,
                    toolDtoId = it.toolDto.id,
                    originTags = it.tags,
                    tags = "",
                    originCount = it.count,
                    goodCount = it.count,
                    faultCount = 0,
                    damageCount = 0,
                    lossCount = 0,
                    comment = "",
                    isSelected = false
                )
            }.toMutableList()
        )

        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        confirmBtn.setOnClickListener {}
        return view
    }

    private fun confirm(){
        val type = Constants.BluetoothMessageType.RETURN_SHEET_FORM
        val data = gson.toJson(ReturnSheetFormDto(
            outstandingRentalSheet.rentalSheetDto.id,
            outstandingRentalSheet.rentalSheetDto.workerDto.id,
            sharedViewModel.loginManager!!.id,
            outstandingRentalSheet.rentalSheetDto.toolboxDto.id,
            (recyclerView.adapter as OutstandingDetailAdapter).getResult().map{
                ReturnToolFormDto(it.rentalToolDtoId,it.toolDtoId,it.tags,it.goodCount,it.faultCount,it.damageCount,it.lossCount,it.comment)
            }
        ))
        (requireActivity() as MainActivity).bluetoothManager?.send(type,data)
    }

    override fun handleInput(input: String) {
        val type = Constants.BluetoothMessageType.TAG
        val data = "{\"tag\":\"${input}\"}"
        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
    }

    override fun handleResponse(response: Any) {
        if (response is TagDto)
            (recyclerView.adapter as RentalRequestToolAdapter).tagAdded(response)
    }

    override fun onResume() {
        super.onResume()
        TagService.getInstance().inputHandler=this
    }

    override fun onDetach() {
        super.onDetach()
        TagService.getInstance().inputHandler=null
    }

}