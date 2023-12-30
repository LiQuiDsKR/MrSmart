package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
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
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.liquidskr.btclient.WorkerOutstandingDetailAdapter
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import java.lang.reflect.Type

class WorkerOutstandingDetailFragment(outstandingRentalSheet: OutstandingRentalSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalToolDto> = outstandingRentalSheet.rentalSheetDto.toolList

    val outstandingRentalSheet: OutstandingRentalSheetDto = outstandingRentalSheet
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var returnerName: TextView
    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private lateinit var confirmBtn: LinearLayout
    private lateinit var backButton: ImageButton


    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_return_detail, container, false)

        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        returnerName = view.findViewById(R.id.returnerName)
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.confirmBtn)
        backButton = view.findViewById(R.id.backButton)

        returnerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        workerName.text = outstandingRentalSheet.rentalSheetDto.workerDto.name
        leaderName.text = outstandingRentalSheet.rentalSheetDto.leaderDto.name
        timeStamp.text = outstandingRentalSheet.rentalSheetDto.eventTimestamp

        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is WorkerOutstandingDetailAdapter) {
                    bluetoothManager.requestData(RequestType.RETURN_SHEET_REQUEST, "{outstandingRentalSheetId:${outstandingRentalSheet.id}}", object:
                        BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "반납 신청 완료", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        var existToolList: MutableList<RentalToolDto> = mutableListOf() // 0인 항목 미표시
        for (rentalTool in toolList) {
            if (rentalTool.outstandingCount > 0) {
                existToolList.add(rentalTool)
            }
        }

        val adapter = WorkerOutstandingDetailAdapter(existToolList)
        recyclerView.adapter = adapter

        return view
    }
}