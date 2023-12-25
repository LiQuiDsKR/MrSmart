package com.liquidskr.btclient

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestToolDto

class RentalRequestToolAdapter(val rentalRequestTools: List<RentalRequestToolDto>) :
    RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {
    var selectedToolsToRental: MutableList<Long> = mutableListOf()
    class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentaltool, parent, false)
        return RentalRequestToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RentalRequestToolViewHolder, position: Int) {
        val currentRentalRequestTool = rentalRequestTools[position]
        holder.toolName.text = currentRentalRequestTool.toolDto.name
        holder.toolSpec.text = currentRentalRequestTool.toolDto.spec
        holder.toolCount.text = currentRentalRequestTool.count.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount, currentRentalRequestTool.count)
        }
        holder.itemView.setOnClickListener{// 항목 자체를 눌렀을 때
            if (!(currentRentalRequestTool.id in selectedToolsToRental)) {
                selectedToolsToRental.add(currentRentalRequestTool.id)
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            } else {
                selectedToolsToRental.remove(currentRentalRequestTool.id)
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            }
        }
    }

    override fun getItemCount(): Int {
        return rentalRequestTools.size
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

}