package com.liquidskr.btclient

import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolApproveFormSelectedDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tool.ToolService

class RentalRequestToolAdapter(
    private var items : MutableList<RentalRequestToolApproveFormSelectedDto>
) : RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {

    inner class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
    }

    /**
     * simply inflate.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentalrequesttool, parent, false)
        return RentalRequestToolViewHolder(itemView)
    }

    /**
     * each viewHolder initialize
     * 1. bind datas
     * 2. addEventListener
     */
    override fun onBindViewHolder(holder: RentalRequestToolViewHolder, position: Int) {
        val toolFormDto = items[position]
        val toolDtoId = toolFormDto.toolDtoId
        val toolDto = ToolService.getInstance().getToolById(toolDtoId)

        holder.toolName.text = toolDto.name
        holder.toolSpec.text = toolDto.spec
        holder.toolCount.text = toolFormDto.count.toString()
        holder.toolCount.setOnClickListener { // count 부분을 눌렀을 떄
            showCountSelectDialog(holder.toolCount, toolFormDto)
        }
        if (items[position].isSelected==false){
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        } else if (items[position].isSelected==true){
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        }
        holder.itemView.setOnClickListener {
            if (items[position].isSelected==true){
                items[position].isSelected=false
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            } else if (items[position].isSelected==false){
                items[position].isSelected=true
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            }
        }
    }

    /**
     * count info needed
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * count edit. - viewholder-toolCount view click event
     */
    private fun showCountSelectDialog(textView: TextView, toolDto: RentalRequestToolApproveFormSelectedDto) {
        DialogUtils.showTextDialog("공구 개수 변경",textView.text.toString(), InputType.TYPE_CLASS_NUMBER) {
            textView.text = it
            items.find{it.toolDtoId==toolDto.toolDtoId}?.count=it.toInt()
        }
    }

    /**
     * tagAdded
     */
    fun tagAdded(tag: TagDto) {
        Log.d("tagAdded","before added : " + tag.macaddress + " / " + items.toString())
        val item = items.find{it.toolDtoId==tag.toolDto.id}?:null
        if (item!=null && !item.isSelected){
            item.isSelected=true
            if (item.tags.contains(tag.macaddress)){
                Log.d("tagAdded","not added : " + tag.macaddress + " / " + items.toString())
            }else if (item.tags.length>1){
                item.tags=item.tags+","+tag.macaddress
            }else {
                item.tags = tag.macaddress
            }
            notifyItemChanged(items.indexOf(item))
        }
        Log.d("tagAdded","after added : " + tag.macaddress + " / " + items.toString())
    }
    fun areAllSelected() : Boolean{
        return items.all{it.isSelected}
    }
    fun isNothingSelected():Boolean{
        return items.all{!it.isSelected}
    }

    /**
     * selected all.
     */
    fun getResult():List<RentalRequestToolApproveFormSelectedDto>{
        return items.filter{ it.isSelected }
    }
}