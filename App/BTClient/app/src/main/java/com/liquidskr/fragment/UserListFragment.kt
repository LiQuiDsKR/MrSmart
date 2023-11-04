package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.UserListAdapter

class UserListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_userlist, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        /*
        // 어댑터 초기화
        val adapter = UserListAdapter(requireContext())

        // RecyclerView와 어댑터 연결
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 데이터를 어댑터에 추가 (예: 데이터베이스에서 사용자 목록을 가져와 추가)
        val dbHelper = DatabaseHelper(requireContext())
        val userList = dbHelper.getAllUsers() // 적절한 데이터 가져오기
        adapter.submitList(userList) // 어댑터에 데이터 제출
    */
        return rootView
    }

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }
}
