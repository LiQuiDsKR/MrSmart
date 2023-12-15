package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalToolDto

class WorkerOutstandingDetailAdapter(val outstandingRentalTools: List<RentalToolDto>) :
    RecyclerView.Adapter<WorkerOutstandingDetailAdapter.OutstandingRentalToolViewHolder>() {
    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ReturnToolName)
        var toolCount: TextView = itemView.findViewById(R.id.ReturnToolCount)
        var toolSpec: TextView = itemView.findViewById(R.id.ReturnToolSpec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_returntool, parent, false)
        return OutstandingRentalToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutstandingRentalToolViewHolder, position: Int) {
        val currentOutstandingRentalTool = outstandingRentalTools[position]
        holder.toolName.text = currentOutstandingRentalTool.toolDto.name
        holder.toolCount.text = currentOutstandingRentalTool.outstandingCount.toString()
        holder.toolSpec.text = currentOutstandingRentalTool.toolDto.spec
    }

    override fun getItemCount(): Int {
        return outstandingRentalTools.size
    }
}