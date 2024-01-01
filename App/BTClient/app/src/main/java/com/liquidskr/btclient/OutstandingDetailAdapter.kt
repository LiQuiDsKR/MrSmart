package com.liquidskr.btclient

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.RentalToolDto

class OutstandingDetailAdapter(private val recyclerView: RecyclerView, val outstandingRentalTools: List<RentalToolDto>) :
    RecyclerView.Adapter<OutstandingDetailAdapter.OutstandingRentalToolViewHolder>() {
    val selectedToolsToReturn: MutableList<Long> = mutableListOf()
    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_returntool, parent, false)
        return OutstandingRentalToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OutstandingRentalToolViewHolder, position: Int) {
        val currentOutstandingRentalTool = outstandingRentalTools[position]
        holder.toolName.text = currentOutstandingRentalTool.toolDto.name
        holder.toolCount.text = currentOutstandingRentalTool.outstandingCount.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount, currentOutstandingRentalTool.outstandingCount)
        }
        holder.toolSpec.text = currentOutstandingRentalTool.toolDto.spec
        holder.itemView.setOnClickListener {
            handleSelection(currentOutstandingRentalTool)
        }
        if (isSelected(currentOutstandingRentalTool)) {
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }
    }

    override fun getItemCount(): Int {
        return outstandingRentalTools.size
    }

    private fun showNumberDialog(textView: TextView, maxCount: Int) {
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
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    fun handleSelection(currentRentalTool: RentalToolDto) {
        if (!isSelected(currentRentalTool)) {
            addToSelection(currentRentalTool)
        } else {
            removeFromSelection(currentRentalTool)
        }
        notifyDataSetChanged() // 변경된 데이터를 알림
    }
    fun tagAdded(currentRentalTool: RentalToolDto) {
        Log.d("Tst", "imhere")
        addToSelection(currentRentalTool)
        Log.d("Tst", "imhere2")
        recyclerView.post {
            notifyDataSetChanged()
        }
        Log.d("Tst", "imhere3")
    }

    private fun isSelected(currentRentalTool: RentalToolDto): Boolean {
        return currentRentalTool.id in selectedToolsToReturn
    }

    private fun addToSelection(currentRentalTool: RentalToolDto) {
        selectedToolsToReturn.add(currentRentalTool.id)

    }

    private fun removeFromSelection(currentRentalTool: RentalToolDto) {
        selectedToolsToReturn.remove(currentRentalTool.id)
    }
}