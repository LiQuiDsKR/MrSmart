package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.MembershipAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipSQLite


class WorkerMembershipFindFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView

    private lateinit var editTextName: EditText
    private lateinit var searchBtn: ImageButton

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_membership_list, container, false)
        editTextName = view.findViewById(R.id.editTextName)
        searchBtn = view.findViewById(R.id.SearchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // DatabaseHelper 인스턴스 생성
        val databaseHelper = DatabaseHelper(requireContext())
        val memberships: List<MembershipSQLite> = databaseHelper.getAllMemberships()

        val adapter = MembershipAdapter(memberships) { membership ->
            sharedViewModel.leader = membership
            Log.d("test", membership.toString() + "///" + sharedViewModel.leader.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }
        searchBtn.setOnClickListener {
            filterByName(adapter, memberships, editTextName.text.toString())
        }

        recyclerView.adapter = adapter

        return view
    }

    fun filterByName(adapter: MembershipAdapter, memberships: List<MembershipSQLite>, keyword: String) {
        val newList: MutableList<MembershipSQLite> = mutableListOf()
        for (membership in memberships) {
            if (keyword in membership.name) {
                newList.add(membership)
            }
        }
        adapter.updateList(newList)
    }
    companion object {
        private var type: Int = 0

        fun getType(): Int {
            return type
        }
        fun newInstance(type: Int): WorkerMembershipFindFragment {
            val fragment = WorkerMembershipFindFragment()
            this.type = type
            return fragment
        }
    }
}