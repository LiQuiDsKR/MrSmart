package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.OutstandingState
import com.mrsmart.standard.rental.RentalToolDto


class OutstandingRentalSheetAdapter(private var outstandingRentalSheets: List<OutstandingRentalSheetDto>, private val onItemClick: (OutstandingRentalSheetDto) -> Unit) :
    RecyclerView.Adapter<OutstandingRentalSheetAdapter.OutstandingRentalSheetViewHolder>() {

    class OutstandingRentalSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_TimeStamp)
        var sheetState: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_sheetState)
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
        if (currentOutstandingRentalSheet.outstandingStatus == OutstandingState.READY) holder.sheetState.text = "미신청 (신청 대기)"
        if (currentOutstandingRentalSheet.outstandingStatus == OutstandingState.REQUEST) holder.sheetState.text = "신청 완료 (승인 대기)"
        var toolListString = ""
        for (tool: RentalToolDto in currentOutstandingRentalSheet.rentalSheetDto.toolList) {
            val toolName: String = tool.toolDto.name
            val toolCount: Int = tool.outstandingCount
            if (toolCount > 0) {
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
        }
        holder.toolListTextView.text = toolListString
        holder.itemView.setOnClickListener {
            onItemClick(currentOutstandingRentalSheet)
        }
    }
    override fun getItemCount(): Int {
        return outstandingRentalSheets.size
    }
    fun updateList(newList: List<OutstandingRentalSheetDto>) {
        outstandingRentalSheets = newList
        notifyDataSetChanged()
    }


}