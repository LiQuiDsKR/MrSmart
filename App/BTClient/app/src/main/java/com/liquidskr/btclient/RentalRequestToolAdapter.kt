package com.liquidskr.btclient

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets.Side.all
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.RentalRequestToolApproveFormDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolService

class RentalRequestToolAdapter(
    private var items : MutableList<RentalRequestToolApproveFormDto>
) : RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {

    private var selection : MutableMap<Long,Boolean> = items.associate { it.toolDtoId to false }.toMutableMap()

    inner class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val toolFormDto = items[position]
        val toolDtoId = toolFormDto.toolDtoId
        val toolDto = ToolService.getInstance().getToolById(toolDtoId)

        holder.toolName.text = toolDto.name
        holder.toolSpec.text = toolDto.spec
        holder.toolCount.text = toolFormDto.count.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showNumberDialog(holder.toolCount, toolFormDto)
        }
        holder.itemView.setOnClickListener {
            if (selection[toolDtoId]==true) {
                selection[toolDtoId]=false
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            } else if (selection[toolDtoId] == false){
                selection[toolDtoId]=true
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    private fun showNumberDialog(textView: TextView, toolDto: RentalRequestToolApproveFormDto) {
        DialogUtils.showTextDialog("공구 개수 변경",textView.text.toString(), InputType.TYPE_CLASS_NUMBER) {
            textView.text = it
        }
//        val builder = AlertDialog.Builder(textView.context)
//        builder.setTitle("공구 개수 변경")
//        val input = NumberPicker(textView.context)
//        input.minValue = 1
//        input.maxValue = toolDto.rentalRequestTool.count
//        input.wrapSelectorWheel = false
//        input.value = textView.text.toString().toInt()
//
//        builder.setView(input)
//
//        builder.setPositiveButton("확인") { _, _ ->
//            val newValue = input.value.toString()
//            // 여기서 숫자 값을 처리하거나 다른 작업을 수행합니다.
//            textView.text = newValue
//            for (rentalRequestTool in rentalRequestToolWithCounts) {
//                if (toolDto.rentalRequestTool.id == rentalRequestTool.rentalRequestTool.id) {
//                    rentalRequestTool.count = input.value
//                }
//            }
//        }
//
//        builder.setNegativeButton("취소") { dialog, _ ->
//            dialog.cancel()
//        }
//
//        builder.show()
    }
    fun tagAdded(tag: TagDto) {
        if (tag.toolDto.id in selection && selection[tag.toolDto.id]!=true){
            selection[tag.toolDto.id]=true
            val item = items.find{it.toolDtoId==tag.toolDto.id}!!
            if (item.tags.length>1){
                item.tags=item.tags+","+tag.macaddress
            }else{
                item.tags=tag.macaddress
            }
            notifyDataSetChanged()
        }
    }
    fun areAllSelected() : Boolean{
        return selection.values.all{it}
    }
    fun isNothingSelected():Boolean{
        return selection.values.all{!it}
    }
    fun getResult():List<RentalRequestToolApproveFormDto>{
        return items.filter{ selection[it.toolDtoId]?:false }
    }
}