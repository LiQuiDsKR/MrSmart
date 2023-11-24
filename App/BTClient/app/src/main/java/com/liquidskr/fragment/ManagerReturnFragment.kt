package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.Membership

class ManagerReturnFragment() : Fragment() {
    lateinit var searchTypeSpinner: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        searchTypeSpinner = view.findViewById(R.id.SearchTypeSpinner)

        val category1Data = arrayOf("리더명", "대여자명", "공기구명")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category1Data)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchTypeSpinner.adapter = adapter1


        return view
    }
}