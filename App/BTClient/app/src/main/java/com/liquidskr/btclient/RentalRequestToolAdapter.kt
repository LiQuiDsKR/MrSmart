package com.liquidskr.btclient

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.tool.RentalRequestToolWithCount
import com.mrsmart.standard.tool.ToolWithCount
import com.mrsmart.standard.toolbox.ToolboxService

class RentalRequestToolAdapter(var rentalRequestToolWithCounts: MutableList<RentalRequestToolWithCount>) :
    RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {
    var selectedToolsToRental: MutableList<Long> = mutableListOf()

    class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentalrequesttool, parent, false)
        return RentalRequestToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentalRequestToolViewHolder, position: Int) {
        val currentRentalRequestToolWithCount = rentalRequestToolWithCounts[position]
        holder.toolName.text = currentRentalRequestToolWithCount.rentalRequestTool.toolDto.name
        holder.toolSpec.text = currentRentalRequestToolWithCount.rentalRequestTool.toolDto.spec
        holder.toolCount.text = currentRentalRequestToolWithCount.count.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount, currentRentalRequestToolWithCount)
        }
        holder.itemView.setOnClickListener {
            handleSelection(currentRentalRequestToolWithCount.rentalRequestTool.toolDto.id)
        }
        if (isSelected(currentRentalRequestToolWithCount.rentalRequestTool.toolDto.id)) {
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }
    }

    override fun getItemCount(): Int {
        return rentalRequestToolWithCounts.size
    }
    private fun showNumberDialog(textView: TextView, rentalRequestToolWithCount: RentalRequestToolWithCount) {
        val builder = AlertDialog.Builder(textView.context)
        builder.setTitle("공구 개수 변경")
        val input = NumberPicker(textView.context)
        input.minValue = 1
        input.maxValue = rentalRequestToolWithCount.rentalRequestTool.count
        input.wrapSelectorWheel = false
        input.value = textView.text.toString().toInt()

        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val newValue = input.value.toString()
            // 여기서 숫자 값을 처리하거나 다른 작업을 수행합니다.
            textView.text = newValue
            for (rentalRequestTool in rentalRequestToolWithCounts) {
                if (rentalRequestToolWithCount.rentalRequestTool.id == rentalRequestTool.rentalRequestTool.id) {
                    rentalRequestTool.count = input.value
                }
            }
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    fun handleSelection(toolId: Long) {
        if (!isSelected(toolId)) {
            addToSelection(toolId)
        } else {
            removeFromSelection(toolId)
        }
        notifyDataSetChanged() // 변경된 데이터를 알림
    }
    fun tagAdded(toolId: Long) {
        addToSelection(toolId)
        notifyDataSetChanged()
    }
    fun updateList(newList: MutableList<RentalRequestToolWithCount>) {
        rentalRequestToolWithCounts = newList
        notifyDataSetChanged()
    }

    private fun isSelected(toolId: Long): Boolean {
        return toolId in selectedToolsToRental
    }

    private fun addToSelection(toolId: Long) {
        selectedToolsToRental.add(toolId)
    }

    private fun removeFromSelection(toolId: Long) {
        selectedToolsToRental.remove(toolId)
    }
}