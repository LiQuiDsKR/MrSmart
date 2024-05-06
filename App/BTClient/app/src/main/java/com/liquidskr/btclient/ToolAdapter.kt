package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolSQLite

class ToolAdapter(var tools: List<ToolSQLite>) :
    RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {
    private val selectedTools: MutableList<ToolSQLite> = mutableListOf()

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolName: TextView = itemView.findViewById(R.id.ToolName)
        val toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        val toolCheck: ImageView = itemView.findViewById(R.id.ToolCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_tool, parent, false)
        return ToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val currentTool = tools[position]
        holder.toolName.text = currentTool.name
        holder.toolSpec.text = currentTool.spec

        var isSelected = selectedTools.contains(currentTool)
        updateViewHolderSelection(holder, isSelected)

        holder.itemView.setOnClickListener {
            val newSelectedState = !isSelected
            updateViewHolderSelection(holder, newSelectedState)
            if (newSelectedState) {
                selectedTools.add(currentTool)
                isSelected = true
            } else {
                selectedTools.remove(currentTool) // 체크해제된 항목은 제거
                isSelected = false
            }
        }
    }
    private fun updateViewHolderSelection(holder: ToolViewHolder, isSelected: Boolean) {
        if (isSelected) {
            holder.toolCheck.setBackgroundResource(R.drawable.icon_choice_ic_choice_round_on)
        } else {
            holder.toolCheck.setBackgroundResource(R.drawable.icon_choice_ic_choice_round_off)
        }
    }

    fun getSelectedTools(): List<ToolSQLite> {
        return selectedTools
    }

    override fun getItemCount(): Int {
        return tools.size
    }
    fun updateList(newList: List<ToolSQLite>) {
        tools = newList
        notifyDataSetChanged()
    }

    fun selectTool(toolId: Long) {
        val tool = tools.find { it.id == toolId }
        if (tool != null) {
            selectedTools.add(tool)
            notifyDataSetChanged()
        }
    }
}