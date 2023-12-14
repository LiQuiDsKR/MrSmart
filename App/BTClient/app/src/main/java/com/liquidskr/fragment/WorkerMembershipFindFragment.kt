package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_membership_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // DatabaseHelper 인스턴스 생성
        val databaseHelper = DatabaseHelper(requireContext())
        val memberships: List<MembershipSQLite> = databaseHelper.getAllMemberships()

        val adapter = MembershipAdapter(memberships) { membership ->
            val type = getType()

            if (type == 1) {
                sharedViewModel.worker = membership
                Log.d("test", membership.toString() + "///" + sharedViewModel.worker.toString())

            } else if (type == 2) {
                sharedViewModel.leader = membership
                Log.d("test", membership.toString() + "///" + sharedViewModel.leader.toString())
            }

            // 새로운 Fragment 생성 (Fragment 백스택)
            val fragment = WorkerRentalFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }

        recyclerView.adapter = adapter

        return view
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