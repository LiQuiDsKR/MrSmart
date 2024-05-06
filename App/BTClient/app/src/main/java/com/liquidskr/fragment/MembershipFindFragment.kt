package com.liquidskr.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.MembershipAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MembershipFindFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private lateinit var memberships: List<MembershipDto>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_membership_list, container, false)
        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val databaseHelper = DatabaseHelper.getInstance()
        val adapter = MembershipAdapter(mutableListOf(), this::onItemClick)

        //Coroutine으로 비동기 로딩 ()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("MembershipFindFragment", "getAllMemberships start")
            memberships = databaseHelper.getAllMemberships().map{membershipSQLite -> membershipSQLite.toMembershipDto()}
            Log.d("MembershipFindFragment", "getAllMemberships end")
            withContext(Dispatchers.Main) {
                // UI 업데이트
                adapter.updateList(memberships)
            }
        }

        searchBtn.setOnClickListener {
            //filter by name
            if (memberships==null) return@setOnClickListener
            val newList: MutableList<MembershipDto> = mutableListOf()
            for (membership in memberships) {
                if (editTextName.text.toString() in membership.name) {
                    newList.add(membership)
                }
            }
            adapter.updateList(newList)
        }

        recyclerView.adapter = adapter

        return view
    }

    private fun onItemClick(membership: MembershipDto) {
        if (type==1){
            requireActivity().supportFragmentManager.setFragmentResult("workerId", Bundle().apply {
                putLong("workerId", membership.id)
            })
        } else if (type==2) {
            requireActivity().supportFragmentManager.setFragmentResult("leaderId", Bundle().apply {
                putLong("leaderId", membership.id)
            })
        } else {
            Log.d("MembershipFindFragment", "type error")
        }
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {
        private var type: Int = 0

        fun newInstance(type: Int): MembershipFindFragment {
            val fragment = MembershipFindFragment()
            this.type = type
            return fragment
        }
    }
}