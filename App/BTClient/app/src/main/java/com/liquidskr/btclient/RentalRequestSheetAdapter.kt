package com.liquidskr.btclient

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestSheetDto

class RentalRequestSheetAdapter(private val rentalRequestSheets: List<RentalRequestSheetDto>) :
    RecyclerView.Adapter<RentalRequestSheetAdapter.RentalRequestSheetViewHolder>() {

    class RentalRequestSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.RentalRequestSheet_RecyclerView)
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
        holder.timeStamp.text = currentRentalRequestSheet.eventTimestamp
        val layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = RentalRequestToolAdapter(currentRentalRequestSheet.toolList)
        Log.d("test", currentRentalRequestSheet.toolList.toString())
        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return rentalRequestSheets.size
    }


}