package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.rental.RentalRequestToolDto


class RentalRequestSheetAdapter(private var rentalRequestSheets: List<RentalRequestSheetDto>, private val onItemClick: (RentalRequestSheetDto) -> Unit) :
    RecyclerView.Adapter<RentalRequestSheetAdapter.RentalRequestSheetViewHolder>() {

    class RentalRequestSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestSheetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentalrequestsheet, parent, false)
        return RentalRequestSheetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentalRequestSheetViewHolder, position: Int) {
        val currentRentalRequestSheet = rentalRequestSheets[position]
        holder.workerName.text = currentRentalRequestSheet.workerDto.name
        holder.leaderName.text = currentRentalRequestSheet.leaderDto.name
        holder.timeStamp.text = currentRentalRequestSheet.eventTimestamp //LocalDateTime.parse(currentRentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        var toolListString = ""
        for (tool:RentalRequestToolDto in currentRentalRequestSheet.toolList) {
            val toolName: String = tool.toolDto.name
            val toolCount: String = tool.count.toString()
            toolListString = toolListString.plus("$toolName($toolCount)  ")
        }
        holder.toolListTextView.text = toolListString
        holder.itemView.setOnClickListener {
            onItemClick(currentRentalRequestSheet)
        }
    }

    override fun getItemCount(): Int {
        return rentalRequestSheets.size
    }
    fun updateList(newList: List<RentalRequestSheetDto>) {
        rentalRequestSheets = newList
        notifyDataSetChanged()
    }


}