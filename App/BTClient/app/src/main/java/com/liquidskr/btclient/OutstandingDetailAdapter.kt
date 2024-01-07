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
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.tool.RentalRequestToolWithCount
import com.mrsmart.standard.tool.RentalToolWithCount
import com.mrsmart.standard.tool.ToolWithCount

class OutstandingDetailAdapter(private val recyclerView: RecyclerView, var outstandingRentalToolWithCounts: MutableList<RentalToolWithCount>,
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
        holder.toolName.text = currentOutstandingRentalToolWithCount.rentalTool.toolDto.name
        holder.toolCount.text = currentOutstandingRentalToolWithCount.count.toString() // 바꾸
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount, currentOutstandingRentalToolWithCount.rentalTool.outstandingCount, currentOutstandingRentalToolWithCount)
        }
        holder.toolSpec.text = currentOutstandingRentalToolWithCount.rentalTool.toolDto.spec
        holder.setToolState.setOnClickListener{
            onSetToolStateClick(currentOutstandingRentalToolWithCount)
        }
        holder.selectSpace.setOnClickListener {
            handleSelection(currentOutstandingRentalToolWithCount)
        }
        if (isSelected(currentOutstandingRentalToolWithCount)) {
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }
    }

    override fun getItemCount(): Int {
        return outstandingRentalToolWithCounts.size
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
    fun handleSelection(currentRentalTool: RentalToolWithCount) {
        if (!isSelected(currentRentalTool)) {
            addToSelection(currentRentalTool)
        } else {
            removeFromSelection(currentRentalTool)
        }
        notifyDataSetChanged() // 변경된 데이터를 알림
    }
    fun tagAdded(currentRentalTool: RentalToolWithCount) {
        Log.d("Tst", "imhere")
        addToSelection(currentRentalTool)
        Log.d("Tst", "imhere2")
        recyclerView.post {
            notifyDataSetChanged()
        }
        Log.d("Tst", "imhere3")
    }
    fun updateList(newList: MutableList<RentalToolWithCount>) {
        outstandingRentalToolWithCounts = newList
        notifyDataSetChanged()
    }

    private fun isSelected(currentRentalTool: RentalToolWithCount): Boolean {
        return currentRentalTool.rentalTool.id in selectedToolsToReturn
    }

    private fun addToSelection(currentRentalTool: RentalToolWithCount) {
        selectedToolsToReturn.add(currentRentalTool.rentalTool.id)

    }

    private fun removeFromSelection(currentRentalTool: RentalToolWithCount) {
        selectedToolsToReturn.remove(currentRentalTool.rentalTool.id)
    }
}