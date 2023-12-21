package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDtoSQLite

class ToolAdapter(var tools: List<ToolDtoSQLite>, val onItemClick: (ToolDtoSQLite) -> Unit) :
    RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {
    private val selectedTools: MutableList<ToolDtoSQLite> = mutableListOf()

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
        var isChecked: Boolean = false
        holder.toolCheck.setOnClickListener {
            isChecked = !isChecked
            if (isChecked) {
                holder.toolCheck.setBackgroundResource(R.drawable.icon_choice_ic_choice_round_on)
                selectedTools.add(currentTool)
            } else {
                holder.toolCheck.setBackgroundResource(R.drawable.icon_choice_ic_choice_round_off)
                selectedTools.remove(currentTool) // 체크해제된 항목은 제거
            }
        }
    }
    fun getSelectedTools(): List<ToolDtoSQLite> {
        return selectedTools
    }

    override fun getItemCount(): Int {
        return tools.size
    }
    fun updateList(newList: List<ToolDtoSQLite>) {
        tools = newList
        notifyDataSetChanged()
    }
}