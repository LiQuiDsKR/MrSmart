package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import java.lang.reflect.Type

class WorkerLobbyFragment(worker: MembershipDto) : Fragment() {
    lateinit var connectBtn: ImageButton
    lateinit var rentalBtn: ImageButton
    lateinit var refreshBtn: ImageButton

    val worker = worker

    lateinit var welcomeMessage: TextView
    val gson = Gson()
    private lateinit var recyclerView: RecyclerView
    private lateinit var bluetoothManager: BluetoothManager
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_lobby, container, false)

        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        connectBtn = view.findViewById(R.id.connectBtn)
        rentalBtn = view.findViewById(R.id.LobbyRentalBtn)
        refreshBtn = view.findViewById(R.id.refreshBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        welcomeMessage.text = worker.name + "님 환영합니다."
        val adapter = OutstandingRentalSheetAdapter(emptyList()) { outstandingRentalSheet ->
            val fragment = WorkerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        refreshBtn.setOnClickListener {
            getOutstandingRentalSheetList()
        }

        rentalBtn.setOnClickListener {
            val lobbyActivity = activity as? LobbyActivity

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            sharedViewModel.worker = MembershipSQLite(0, "", "", "", "", "", "", "", "")
            sharedViewModel.leader = MembershipSQLite(0, "", "", "", "", "", "", "", "")
            sharedViewModel.rentalRequestToolIdList.clear()
            val fragment = WorkerRentalFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        getOutstandingRentalSheetList()
        return view
    }
    fun getOutstandingRentalSheetList() {
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()
        bluetoothManager.requestData(RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP,"{membershipId:${sharedViewModel.loginWorker.id}}",object: BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
                val updatedList: List<OutstandingRentalSheetDto> = gson.fromJson(result, type)
                requireActivity().runOnUiThread {
                    (recyclerView.adapter as OutstandingRentalSheetAdapter).updateList(updatedList)
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
}