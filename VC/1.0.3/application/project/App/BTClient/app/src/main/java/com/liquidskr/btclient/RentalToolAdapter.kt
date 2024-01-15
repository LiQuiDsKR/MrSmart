package com.liquidskr.btclient

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.tool.ToolWithCount

class RentalToolAdapter(var tools: MutableList<ToolWithCount>, private val onDeleteItemClickListener: OnDeleteItemClickListener) :
    RecyclerView.Adapter<RentalToolAdapter.RentalToolViewHolder>() {

    interface OnDeleteItemClickListener {
        fun onDeleteItemClicked(list: MutableList<ToolWithCount>)
    }

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
        holder.id = currentTool.tool.id
        holder.toolName.text = currentTool.tool.name
        holder.toolSpec.text = currentTool.tool.spec
        holder.toolCount.text = currentTool.count.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount) { count ->
                currentTool.count = count
                tools[position].count = count
            }
        }
        holder.deleteBtn.setOnClickListener {
            val newList = tools.toMutableList()
            newList.removeAt(holder.adapterPosition)
            tools = newList
            notifyDataSetChanged()
            onDeleteItemClickListener.onDeleteItemClicked(newList)
        }
    }

    private fun showNumberDialog(textView: TextView, onConfirm: (newValue: Int) -> Unit) {
        val builder = AlertDialog.Builder(textView.context)
        builder.setTitle("공구 개수 변경")
        val input = NumberPicker(textView.context)
        input.minValue = 1
        input.maxValue = 100 // 예를 들어 최대값을 설정
        input.wrapSelectorWheel = false
        input.value = textView.text.toString().toInt()

        builder.setView(input)

        builder.setPositiveButton("확인") { _, _ ->
            val newValue = input.value
            textView.text = newValue.toString()
            onConfirm(newValue)
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    fun updateList(newList: MutableList<ToolWithCount>) {
        tools = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return tools.size
    }
}
