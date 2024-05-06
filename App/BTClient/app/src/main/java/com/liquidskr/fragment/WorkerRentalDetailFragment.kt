package com.liquidskr.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
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
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.WorkerRentalRequestToolAdapter
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormDto
import java.io.IOException
import java.lang.reflect.Type

class WorkerRentalDetailFragment(rentalRequestSheet: RentalRequestSheetDto) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var toolList: List<RentalRequestToolDto> = rentalRequestSheet.toolList

    var rentalRequestSheet: RentalRequestSheetDto = rentalRequestSheet

    private lateinit var workerName: TextView
    private lateinit var leaderName: TextView
    private lateinit var timeStamp: TextView

    private val handler = Handler(Looper.getMainLooper()) // UI블로킹 start
    private lateinit var popupLayout: View
    private lateinit var progressText: TextView
    private var isPopupVisible = false // UI블로킹 end

    private lateinit var backButton: ImageButton

    private lateinit var confirmBtn: LinearLayout
    private lateinit var cancelBtn: LinearLayout
    private lateinit var bluetoothManagerOld: BluetoothManager_Old

    val gson = Gson()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental_detail, container, false)
        bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()
        workerName = view.findViewById(R.id.workerName)
        leaderName = view.findViewById(R.id.leaderName)
        timeStamp = view.findViewById(R.id.timestamp)
        confirmBtn = view.findViewById(R.id.rental_detail_confirmBtn)
        cancelBtn = view.findViewById(R.id.rental_detail_cancelBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton = view.findViewById(R.id.backButton)


        workerName.text = rentalRequestSheet.workerDto.name
        leaderName.text = rentalRequestSheet.leaderDto.name
        timeStamp.text = rentalRequestSheet.eventTimestamp
        //LocalDateTime.parse(rentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var adapter = WorkerRentalRequestToolAdapter(toolList)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        cancelBtn.setOnClickListener {
            sheetCancel()
        }

        confirmBtn.setOnClickListener {
            confirmBtn.isFocusable = false
            confirmBtn.isClickable = false

            recyclerView.adapter?.let { adapter ->
                if (adapter is WorkerRentalRequestToolAdapter) {
                    showPopup() // UI 블로킹
                    var rentalRequestToolFormList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                    for (rentalRequestTool in rentalRequestSheet.toolList) {
                        rentalRequestToolFormList.add(RentalRequestToolFormDto(rentalRequestTool.toolDto.id, rentalRequestTool.count))
                    }
                    val rentalRequestSheetForm = RentalRequestSheetFormDto("DefaultName", rentalRequestSheet.workerDto.id, rentalRequestSheet.leaderDto.id, rentalRequestSheet.toolboxDto.id, rentalRequestToolFormList)
                    try {
                        bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_APPLY, "{rentalRequestSheetId:${rentalRequestSheet.id}}", object:
                            BluetoothManager_Old.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                if (result == "good") {
                                    handler.post {
                                        Toast.makeText(activity, "대여 신청 완료", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                } else {
                                    handler.post {
                                        Toast.makeText(activity, "대여 신청 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    requireActivity().supportFragmentManager.popBackStack()
                                }

                            }

                            override fun onError(e: Exception) {
                                handler.post {
                                    Toast.makeText(activity, "대여 신청 실패. 재연결 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                e.printStackTrace()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        })
                    } catch (e: IOException) {

                    }
                }
            }
        }

        return view
    }
    fun sheetCancel() {
        try {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_CANCEL, "{rentalRequestSheetId:${rentalRequestSheet.id}}", object:
                BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        handler.post {
                            Toast.makeText(activity, "대여 삭제 완료", Toast.LENGTH_SHORT).show()
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        handler.post {
                            Toast.makeText(activity, "대여 삭제 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                }

                override fun onError(e: Exception) {
                    handler.post {
                        Toast.makeText(activity, "대여 삭제 실패. 재연결 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
        } catch(e:Exception) {
            e.printStackTrace()
        }
    }
    fun sheetCancelAfterForm() {
        try {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.RENTAL_REQUEST_SHEET_CANCEL, "{rentalRequestSheetId:${rentalRequestSheet.id}}", object:
                BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        handler.post {
                            Toast.makeText(activity, "대여 신청 후 기존 정보 삭제 실패, 서버가 거부했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                }

                override fun onError(e: Exception) {
                    handler.post {
                        Toast.makeText(activity, "대여 신청 후 기존 정보 삭제 실패. 재연결 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
        } catch(e:Exception) {
            e.printStackTrace()
        }
    }
    private fun showPopup() { // UI 블로킹 end
        isPopupVisible = true
        popupLayout.requestFocus()
        popupLayout.setOnClickListener {

        }
        popupLayout.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                return@setOnKeyListener true
            }
            false
        }
        popupLayout.visibility = View.VISIBLE
    }
    private fun hidePopup() {
        handler.post {
            isPopupVisible = false
            popupLayout.visibility = View.GONE
        }
    } // UI 블로킹 end
}