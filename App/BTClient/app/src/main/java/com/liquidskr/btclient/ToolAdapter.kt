package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDtoSQLite

class ToolAdapter(private val tools: List<ToolDtoSQLite>, private val onItemClick: (ToolDtoSQLite) -> Unit) :
    RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {
    private val selectedTools: MutableList<ToolDtoSQLite> = mutableListOf()

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        val toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        val toolCheck: CheckBox = itemView.findViewById(R.id.ToolCheck)
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

        holder.toolCheck.isChecked = selectedTools.contains(currentTool)
        holder.itemView.setOnClickListener {
            holder.toolCheck.isChecked = !holder.toolCheck.isChecked // toggle check
            if (holder.toolCheck.isChecked) { // 체크된 항목은 추가
                selectedTools.add(currentTool)
            } else {
                selectedTools.remove(currentTool) // 체크해제된 항목은 제거
            }
            onItemClick(currentTool)
        }
    }
    fun getSelectedTools(): List<ToolDtoSQLite> {
        return selectedTools
    }

    override fun getItemCount(): Int {
        return tools.size
    }


}