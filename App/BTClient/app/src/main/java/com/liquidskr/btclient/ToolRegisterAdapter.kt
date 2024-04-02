package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolSQLite

class ToolRegisterAdapter(var tools: List<ToolDto>, private val onItemClick: (ToolDto) -> Unit) :
    RecyclerView.Adapter<ToolRegisterAdapter.ToolViewHolder>() {

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolName: TextView = itemView.findViewById(R.id.RegisterToolList_Name)
        val toolSpec: TextView = itemView.findViewById(R.id.RegisterToolList_Spec)
        val toolCode: TextView = itemView.findViewById(R.id.RegisterToolList_Code)
    }

    /**
     * simply inflate.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_tool_register_tool, parent, false)
        return ToolViewHolder(itemView)
    }

    /**
     * each viewHolder initialize
     * 1. bind datas
     * 2. addEventListener
     */
    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val currentTool = tools[position]
        holder.toolName.text = currentTool.name
        holder.toolSpec.text = currentTool.spec
        holder.toolCode.text = currentTool.code

        holder.itemView.setOnClickListener {
            onItemClick(currentTool)
        }
    }

    /**
     * count info needed
     */
    override fun getItemCount(): Int {
        return tools.size
    }

    fun updateList(newList: List<ToolDto>) {
        tools = newList
        notifyDataSetChanged()
    }
}