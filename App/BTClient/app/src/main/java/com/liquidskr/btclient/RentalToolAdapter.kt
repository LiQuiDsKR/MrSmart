package com.liquidskr.btclient

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolDtoSQLite

class RentalToolAdapter(var tools: List<ToolDtoSQLite>) :
    RecyclerView.Adapter<RentalToolAdapter.RentalToolViewHolder>() {
    val selectedToolsToRental: MutableList<ToolDtoSQLite> = mutableListOf()

    class RentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: Long = 0
        val toolName: TextView = itemView.findViewById(R.id.ToolName)
        val toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        val toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
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
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount)
        }
        holder.itemView.setOnClickListener{// 항목 자체를 눌렀을 때
            if (!(currentTool in selectedToolsToRental)) { // selectedToolsToRental에 currentTool이 없다면
                selectedToolsToRental.add(currentTool)
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            } else {
                selectedToolsToRental.remove(currentTool)
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            }
        }
        holder.deleteBtn.setOnClickListener {
            var toolIdList = mutableListOf<Long>()
            for (tool in selectedToolsToRental) {
                toolIdList.add(tool.id)
            }
            val toolIdToRemove = holder.id
            val indexOfToolToRemove = toolIdList.indexOf(toolIdToRemove)
            if (indexOfToolToRemove != -1) {
                selectedToolsToRental.removeAt(indexOfToolToRemove)
            }
            val newList = tools.toMutableList()
            newList.removeAt(holder.adapterPosition)
            tools = newList
            notifyDataSetChanged()
        }
    }

    fun selectAllTools(recyclerView: RecyclerView, adapter: RentalToolAdapter) {
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

    private fun showNumberDialog(textView: TextView) {
        val builder = AlertDialog.Builder(textView.context)
        builder.setTitle("공구 개수 변경")
        val input = NumberPicker(textView.context)
        input.minValue = 1
        input.maxValue = 100 // 예를 들어 최대값을 설정
        input.wrapSelectorWheel = false
        input.value = textView.text.toString().toInt()

        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val newValue = input.value.toString()
            textView.text = newValue
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    fun updateList(newList: List<ToolDtoSQLite>) {
        tools = newList
        notifyDataSetChanged()
    }
    fun updateCnt(toolId:Long, newCnt:Int) {
        // 해당 toolId에 대한 위치를 찾기

    }
    override fun getItemCount(): Int {
        return tools.size
    }
}
