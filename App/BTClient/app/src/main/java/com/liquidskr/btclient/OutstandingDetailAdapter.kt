package com.liquidskr.btclient

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.tool.RentalRequestToolWithCount
import com.mrsmart.standard.tool.RentalToolWithCount
import com.mrsmart.standard.tool.ToolWithCount

class OutstandingDetailAdapter(private val recyclerView: RecyclerView,
                               var outstandingRentalToolWithCounts: MutableList<RentalToolWithCount>,
                               private val onSetToolStateClick: (RentalToolWithCount) -> Unit) :
    RecyclerView.Adapter<OutstandingDetailAdapter.OutstandingRentalToolViewHolder>() {
    val selectedToolsToReturn: MutableList<Long> = mutableListOf()
    val tools: List<RentalToolWithCount> = outstandingRentalToolWithCounts

    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setToolState: LinearLayout = itemView.findViewById(R.id.toolStateSet)
        val selectSpace: LinearLayout = itemView.findViewById(R.id.selectSpace)

        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolState: TextView = itemView.findViewById(R.id.ToolState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_returntool, parent, false)
        return OutstandingRentalToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutstandingRentalToolViewHolder, position: Int) {
        val currentOutstandingRentalToolWithCount = outstandingRentalToolWithCounts[position]
        var counts = intArrayOf(0,0,0,0)

        holder.toolName.text = currentOutstandingRentalToolWithCount.rentalTool.toolDto.name
        holder.toolCount.text = currentOutstandingRentalToolWithCount.count.toString()
        holder.toolSpec.text = currentOutstandingRentalToolWithCount.rentalTool.toolDto.spec
        holder.setToolState.setOnClickListener{
            onSetToolStateClick(currentOutstandingRentalToolWithCount)
        }
        holder.selectSpace.setOnClickListener {
            handleSelection(currentOutstandingRentalToolWithCount.rentalTool.toolDto.id)
        }
        if (isSelected(currentOutstandingRentalToolWithCount.rentalTool.toolDto.id)) {
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }
        // holder.toolState.text = "양호 : ${currentOutstandingRentalToolWithCount.rentalTool.outstandingCount}"
    }

    override fun getItemCount(): Int {
        return outstandingRentalToolWithCounts.size
    }

    fun updateToolState(toolId: Long, toolStates: IntArray) {
        var stateString = ""
        if (toolStates[0] > 0) stateString += " 양호:${toolStates[0]}"
        if (toolStates[1] > 0) stateString += " 고장:${toolStates[1]}"
        if (toolStates[2] > 0) stateString += " 파손:${toolStates[2]}"
        if (toolStates[3] > 0) stateString += " 망실:${toolStates[3]}"
        for (ortwc in outstandingRentalToolWithCounts) {
            if (toolId == ortwc.rentalTool.toolDto.id) {
                // 해당 툴 아이디를 가진 항목을 찾았을 때
                val position = outstandingRentalToolWithCounts.indexOf(ortwc)
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                if (viewHolder is OutstandingRentalToolViewHolder) {
                    viewHolder.toolState.text = stateString
                }
                notifyDataSetChanged()
                break
            }
        }
    }

    private fun showNumberDialog(textView: TextView, maxCount: Int, rentalToolWithCount: RentalToolWithCount) {
        val builder = AlertDialog.Builder(textView.context)
        builder.setTitle("공구 개수 변경")
        val input = NumberPicker(textView.context)
        input.minValue = 1
        input.maxValue = maxCount // 최댓값은 공구수
        input.wrapSelectorWheel = false
        input.value = textView.text.toString().toInt()

        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val newValue = input.value.toString()
            // 여기서 숫자 값을 처리하거나 다른 작업을 수행합니다.
            textView.text = newValue
            for (ortwc in outstandingRentalToolWithCounts) {
                if (ortwc.rentalTool.id == rentalToolWithCount.rentalTool.id) {
                    ortwc.count = input.value
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
    fun updateList(newList: MutableList<RentalToolWithCount>) {
        outstandingRentalToolWithCounts = newList
        notifyDataSetChanged()
    }

    private fun isSelected(toolId: Long): Boolean {
        return toolId in selectedToolsToReturn
    }

    private fun addToSelection(toolId: Long) {
        selectedToolsToReturn.add(toolId)
    }

    private fun removeFromSelection(toolId: Long) {
        selectedToolsToReturn.remove(toolId)
    }
}