package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.sheet.rental.RentalToolDto

class WorkerOutstandingDetailAdapter(private val outstandingRentalTools: MutableList<RentalToolDto>) : // 굳이 MutableList로 선언할 필요는 없지만 귀찮음
    RecyclerView.Adapter<WorkerOutstandingDetailAdapter.OutstandingRentalToolViewHolder>() {

    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_returntool_worker, parent, false)
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