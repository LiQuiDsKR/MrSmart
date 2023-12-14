package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDtoSQLite

class ToolRegisterAdapter(var tools: List<ToolDtoSQLite>, val onItemClick: (ToolDtoSQLite) -> Unit) :
    RecyclerView.Adapter<ToolRegisterAdapter.ToolViewHolder>() {

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolName: TextView = itemView.findViewById(R.id.RegisterToolList_Name)
        val toolSpec: TextView = itemView.findViewById(R.id.RegisterToolList_Spec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_tool_register_tool, parent, false)
        return ToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val currentTool = tools[position]
        holder.toolName.text = currentTool.name
        holder.toolSpec.text = currentTool.spec

        holder.itemView.setOnClickListener {
            onItemClick(currentTool)
        }
    }
    override fun getItemCount(): Int {
        return tools.size
    }
    fun updateList(newList: List<ToolDtoSQLite>) {
        tools = newList
        notifyDataSetChanged()
    }
}