package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestToolDto

class RentalRequestToolAdapter(private val rentalRequestTools: List<RentalRequestToolDto>) :
    RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {

    class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.RentalRequestTool_Name)
        var toolCount: TextView = itemView.findViewById(R.id.RentalRequestTool_Count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentalrequesttool, parent, false)
        return RentalRequestToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentalRequestToolViewHolder, position: Int) {
        val currentRentalRequestTool = rentalRequestTools[position]
        holder.toolName.text = currentRentalRequestTool.toolDto.name
        holder.toolCount.text = currentRentalRequestTool.count.toString()
    }

    override fun getItemCount(): Int {
        return rentalRequestTools.size
    }


}