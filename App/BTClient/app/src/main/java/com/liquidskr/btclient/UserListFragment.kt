package com.liquidskr.btclient

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserListFragment(private val context: Context) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_userlist, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        // Create a list of users
        val dbHelper = DatabaseHelper(context)
        val userList = dbHelper.getAllUsers() // Initialize your user list

        // Create and set the adapter
        val adapter = UserListAdapter(userList)
        recyclerView.adapter = adapter

        // Set the layout manager (e.g., LinearLayoutManager)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        return rootView
    }
    companion object {
        fun newInstance(): WorkerFragment {
            return WorkerFragment()
        }
    }
}