package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalToolDto

class OutstandingRentalToolAdapter(private val outstandingRentalTools: List<RentalToolDto>) :
    RecyclerView.Adapter<OutstandingRentalToolAdapter.OutstandingRentalToolViewHolder>() {

    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.RentalRequestTool_Name)
        var toolCount: TextView = itemView.findViewById(R.id.RentalRequestTool_Count)

        init {
            itemView.setOnClickListener {
                if (itemView.parent is View) {
                    (itemView.parent as View).performClick() // 클릭 이벤트를 상위View로 전송
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_outstanding_rentaltool, parent, false)
        return OutstandingRentalToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutstandingRentalToolViewHolder, position: Int) {
        val currentOutstandingRentalTool = outstandingRentalTools[position]
        holder.toolName.text = currentOutstandingRentalTool.toolDto.name
        holder.toolCount.text = currentOutstandingRentalTool.count.toString()
    }

    override fun getItemCount(): Int {
        return outstandingRentalTools.size
    }
}
