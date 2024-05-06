package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.membership.MembershipSQLite

class MembershipAdapter(var memberships: List<MembershipDto>, private val onItemClick: (MembershipDto) -> Unit) :
    RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder>() {

    class MembershipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val membershipName: TextView = itemView.findViewById(R.id.MembershipName)
        val membershipPart: TextView = itemView.findViewById(R.id.MembershipPart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_membership, parent, false)
        return MembershipViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MembershipViewHolder, position: Int) {
        val currentMembership = memberships[position]
        holder.membershipName.text = currentMembership.name
        holder.membershipPart.text = "${currentMembership.partDto.subPartDto.mainPartDto.name} / ${currentMembership.partDto.subPartDto.name} / ${currentMembership.partDto.name}"

        holder.itemView.setOnClickListener {
            onItemClick(currentMembership)
        }
    }

    override fun getItemCount(): Int {
        return memberships.size
    }
    fun updateList(newList: List<MembershipDto>) {
        memberships = newList
        notifyDataSetChanged()
    }
}