package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.Membership

class WorkerLobbyFragment(worker: Membership) : Fragment() {
    lateinit var connectBtn: ImageButton
    lateinit var rentalBtn: ImageButton
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_lobby, container, false)

        rentalBtn = view.findViewById(R.id.LobbyRentalBtn)

        rentalBtn.setOnClickListener {
            val lobbyActivity = activity as? LobbyActivity
            val workerRentalFragment = lobbyActivity?.workerRentalFragment

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            workerRentalFragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }
}