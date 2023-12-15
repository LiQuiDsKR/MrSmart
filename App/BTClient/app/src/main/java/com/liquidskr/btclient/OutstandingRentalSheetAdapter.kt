package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto


class OutstandingRentalSheetAdapter(private val outstandingRentalSheets: List<OutstandingRentalSheetDto>, private val onItemClick: (OutstandingRentalSheetDto) -> Unit) :
    RecyclerView.Adapter<OutstandingRentalSheetAdapter.OutstandingRentalSheetViewHolder>() {

    class OutstandingRentalSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalSheetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_outstanding_rentalsheet, parent, false)
        return OutstandingRentalSheetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutstandingRentalSheetViewHolder, position: Int) {
        val currentOutstandingRentalSheet = outstandingRentalSheets[position]
        holder.workerName.text = currentOutstandingRentalSheet.rentalSheetDto.workerDto.name
        holder.leaderName.text = currentOutstandingRentalSheet.rentalSheetDto.leaderDto.name
        holder.timeStamp.text = currentOutstandingRentalSheet.rentalSheetDto.eventTimestamp
        var toolListString = ""
        for (tool: RentalToolDto in currentOutstandingRentalSheet.rentalSheetDto.toolList) {
            val toolName: String = tool.toolDto.name
            val toolCount: String = tool.count.toString()
            toolListString = toolListString.plus("$toolName($toolCount)  ")
        }
        holder.toolListTextView.text = toolListString
        holder.itemView.setOnClickListener {
            onItemClick(currentOutstandingRentalSheet)
        }
    }


    override fun getItemCount(): Int {
        return outstandingRentalSheets.size
    }


}