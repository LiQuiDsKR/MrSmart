package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDtoSQLite

class ToolRegisterListAdapter(val tools: List<ToolDtoSQLite>) :
    RecyclerView.Adapter<ToolRegisterListAdapter.RentalToolViewHolder>() {
    val selectedToolsToRental: MutableList<ToolDtoSQLite> = mutableListOf()

    class RentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: Long = 0
        val toolName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        val toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        val toolCount: TextView = itemView.findViewById(R.id.ToolCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentaltool, parent, false)
        return RentalToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentalToolViewHolder, position: Int) {
        val currentTool = tools[position]
        holder.id = currentTool.id
        holder.toolName.text = currentTool.name
        holder.toolSpec.text = currentTool.spec
        holder.toolCount.text = "1" // count 값을 표시

        holder.itemView.setOnClickListener{// 항목 자체를 눌렀을 때
            if (!(currentTool in selectedToolsToRental)) { // selectedToolsToRental에 currentTool이 없다면
                selectedToolsToRental.add(currentTool)
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            } else {
                selectedToolsToRental.remove(currentTool)
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            }
        }
    }

    fun selectAllTools(recyclerView: RecyclerView, adapter: ToolRegisterListAdapter) {
        selectedToolsToRental.clear()
        selectedToolsToRental.addAll(adapter.tools) // Add all tools to selectedToolsToRental

        // Update the background color of all items in the adapter
        for (i in adapter.tools.indices) {
            val currentTool = adapter.tools[i]
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? RentalToolViewHolder

            // Update UI
            holder?.itemView?.setBackgroundColor(0xFFAACCEE.toInt())
        }
    }


    override fun getItemCount(): Int {
        return tools.size
    }
}
