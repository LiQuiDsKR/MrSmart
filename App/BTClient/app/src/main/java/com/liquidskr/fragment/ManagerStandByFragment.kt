package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.StandByAdapter

class ManagerStandByFragment() : Fragment() {
    private lateinit var recyclerView: RecyclerView
    val gson = Gson()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_standby, container, false)


        var dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.Manager_Return_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = StandByAdapter(dbHelper.getAllStandby())
        recyclerView.adapter = adapter

        return view
    }
}