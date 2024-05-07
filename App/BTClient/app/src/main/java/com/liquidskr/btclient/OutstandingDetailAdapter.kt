package com.liquidskr.btclient

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.DialogUtils.showReturnFormCountSelectDialog
import com.mrsmart.standard.sheet.`return`.ReturnToolFormSelectedDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tool.ToolService

class OutstandingDetailAdapter(
    private var items: MutableList<ReturnToolFormSelectedDto>
) : RecyclerView.Adapter<OutstandingDetailAdapter.OutstandingRentalToolViewHolder>() {

    class OutstandingRentalToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolStateSetButton: LinearLayout = itemView.findViewById(R.id.toolStateSet)
        val selectSpace: LinearLayout = itemView.findViewById(R.id.selectSpace)

        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolState: TextView = itemView.findViewById(R.id.ToolState)
    }

    /**
     * simply inflate.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutstandingRentalToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_returntool, parent, false)
        return OutstandingRentalToolViewHolder(itemView)
    }

    /**
     * each viewHolder initialize
     * 1. bind datas
     * 2. addEventListener
     */
    override fun onBindViewHolder(holder: OutstandingRentalToolViewHolder, position: Int) {
        val toolFormDto = items[position]
        val toolDtoId = toolFormDto.toolDtoId
        val toolDto = ToolService.getInstance().getToolById(toolDtoId)

        holder.toolName.text = toolDto.name
        holder.toolSpec.text = toolDto.spec
        holder.toolCount.text = (
                        toolFormDto.goodCount+
                        toolFormDto.faultCount+
                        toolFormDto.damageCount+
                        toolFormDto.lossCount
                ).toString()
        holder.toolStateSetButton.setOnClickListener{
            DialogUtils.showReturnFormCountSelectDialog(
                count=toolFormDto.originCount,
                goodCountVal=toolFormDto.originCount,
                faultCountVal=0,
                damageCountVal=0,
                lossCountVal=0,
                callback = { goodCount, faultCount, damageCount, lossCount, comment ->
                    holder.toolCount.text = (goodCount+faultCount+damageCount+lossCount).toString()
                    val item = items.find{it.toolDtoId==toolDtoId}?:null
                    if (item != null){
                        item.goodCount=goodCount
                        item.faultCount=faultCount
                        item.damageCount=damageCount
                        item.lossCount=lossCount
                        item.comment=comment
                    }
                    holder.toolState.text="양호 : $goodCount, 불량 : $faultCount, 파손 : $damageCount, 분실 : $lossCount"
                }
            )
        }
        holder.selectSpace.setOnClickListener {
            if (items[position].isSelected==true){
                items[position].isSelected=false
                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
            } else if (items[position].isSelected==false){
                items[position].isSelected=true
                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
            }
        }
        if (items[position].isSelected==false){
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        } else if (items[position].isSelected==true){
            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
        }
    }

    /**
     * count info needed
     */
    override fun getItemCount(): Int {
        return items.size
    }

    fun tagAdded(tag: TagDto) {
        Log.d("tagAdded","before added : " + tag.macaddress + " / " + items.toString())
        val item = items.find{it.toolDtoId==tag.toolDto.id}?:null
        if (item!=null && !item.isSelected && item.originTags.contains(tag.macaddress)){
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
        notifyDataSetChanged()
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
    fun getResult():List<ReturnToolFormSelectedDto>{
        return items.filter{ it.isSelected }
    }
}