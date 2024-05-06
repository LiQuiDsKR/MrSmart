package com.liquidskr.btclient

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolApproveFormSelectedDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestToolFormSelectedDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tool.ToolService

/**
 * ManagerSelfRentalFragment, WorkerSelfRentalFragment에서 사용하는 RecyclerView Adapter
 *
 * 240506 :
 * 아이템 선택의 기능은 직접 신청에서 필요 없다고 판단됨.
 * adapter의 items type은 그대로 selectedFormDto로 유지하겠으나
 * isSelected 속성은 사용하지 않고, 모든 아이템 추가 시엔 isSelected를 true로 설정함.
 */
class RentalRequestToolAdapter(
    private var items : MutableList<RentalRequestToolFormSelectedDto>
) : RecyclerView.Adapter<RentalRequestToolAdapter.RentalRequestToolViewHolder>() {

    inner class RentalRequestToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toolName: TextView = itemView.findViewById(R.id.ToolName)
        var toolSpec: TextView = itemView.findViewById(R.id.ToolSpec)
        var toolCount: TextView = itemView.findViewById(R.id.ToolCount)
        var deleteButton : ImageButton = itemView.findViewById(R.id.deleteBtn)
    }

    /**
     * simply inflate.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalRequestToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rentaltool, parent, false)
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

        /**
         * 240506 : 아래 주석은 isSelected에 따른 색깔 표시 기능. 삭제 예정.
         */
//        if (items[position].isSelected==false){
//            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
//        } else if (items[position].isSelected==true){
//            holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
//        }
//        holder.itemView.setOnClickListener {
//            if (items[position].isSelected==true){
//                items[position].isSelected=false
//                holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
//            } else if (items[position].isSelected==false){
//                items[position].isSelected=true
//                holder.itemView.setBackgroundColor(0xFFAACCEE.toInt())
//            }
//        }

        holder.deleteButton.setOnClickListener {
            items.removeAt(position)
            notifyDataSetChanged()
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
    private fun showCountSelectDialog(textView: TextView, toolDto: RentalRequestToolFormSelectedDto) {
        DialogUtils.showTextDialog("공구 개수 변경",textView.text.toString(), InputType.TYPE_CLASS_NUMBER) {
            textView.text = it
            items.find{it.toolDtoId==toolDto.toolDtoId}?.count=it.toInt()
        }
    }

    /**
     * add Tag to items (: called when tag is scanned.)
     */
    fun addTag(tag: TagDto) {
        Log.d("tagAdded","before added : " + tag.macaddress + " / " + items.toString())
        val item = items.find{it.toolDtoId==tag.toolDto.id}?:null
        if (item==null){
            items.add(RentalRequestToolFormSelectedDto(tag.toolDto.id,1,true))
            notifyItemInserted(items.size-1)
        } else if (!item.isSelected){
            item.isSelected=true
            notifyItemChanged(items.indexOf(item))
        } else {
            item.count++
            notifyItemChanged(items.indexOf(item))
        }
        Log.d("tagAdded","after added : " + tag.macaddress + " / " + items.toString())
    }

    /**
     * add Tool to items (: called when toolboxToolLabel is scanned.)
     */
    fun addTool(toolId:Long){
        val item = items.find{it.toolDtoId==toolId}?:null
        if (item==null){
            items.add(RentalRequestToolFormSelectedDto(toolId,1,true))
            notifyItemInserted(items.size-1)
        } else if (!item.isSelected){
            item.isSelected=true
            notifyItemChanged(items.indexOf(item))
        } else {
            item.count++
            notifyItemChanged(items.indexOf(item))
        }
    }

    fun areAllSelected() : Boolean{
        return items.all{it.isSelected}
    }
    fun isNothingSelected():Boolean{
        return items.all{!it.isSelected}
    }
    fun containsId(id:Long):Boolean{
        return items.any{it.toolDtoId==id}
    }
    fun getCountById(id:Long):Int{
        return items.find{it.toolDtoId==id}?.count?:0
    }

    /**
     * selected all.
     */
    fun getResult():List<RentalRequestToolFormSelectedDto>{
        return items.filter{ it.isSelected }
    }
}