package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class UserListAdapter(private val userList: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_SEARCH = 0
    private val VIEW_TYPE_USER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEARCH -> {
                val searchView = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
                SearchViewHolder(searchView)
            }
            VIEW_TYPE_USER -> {
                val userView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
                UserViewHolder(userView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_SEARCH -> {
                // Bind data for the search view
            }
            VIEW_TYPE_USER -> {
                val user = userList[position - 1] // Subtract 1 for the search view
                (holder as UserViewHolder).bind(user)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_SEARCH
        } else {
            VIEW_TYPE_USER
        }
    }

    override fun getItemCount(): Int {
        return userList.size + 1 // Add 1 for the search view
    }
}

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // Initialize and bind views for the search view
}

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(user: User) {
        // Bind data to the user view
    }
}
