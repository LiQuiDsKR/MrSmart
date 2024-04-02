package com.liquidskr.btclient

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToolRegisterTagDetailAdapter(var tagList: MutableList<String>) :
    RecyclerView.Adapter<ToolRegisterTagDetailAdapter.ToolViewHolder>() {

    private var checkingQR = "default"

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val qrDisplay: TextView = itemView.findViewById(R.id.qr_display)
        val qrDelete: ImageButton = itemView.findViewById(R.id.qr_delete)
        var qrcode: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_tool_register_qr, parent, false)
        return ToolViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val currentQR = tagList[position]
        holder.qrcode = currentQR
        holder.qrDisplay.text = currentQR
        holder.qrDelete.setOnClickListener {
            Log.d("regiAdapter",tagList.toString())
            val updatedList = tagList.toMutableList()
            updatedList.removeAt(holder.adapterPosition)
            tagList = updatedList
            notifyDataSetChanged()
        }
        if (checkingQR == holder.qrcode) {
            // 현재 항목이 qrcode와 일치하면 배경색 변경
            holder.itemView.setBackgroundColor(Color.GREEN)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }, 300)
            checkingQR = "default"
        }
    }
    override fun getItemCount(): Int {
        return tagList.size
    }
    fun qrCheck(qrcode: String) : Boolean {
        for (i in tagList.indices) {
            if (qrcode == tagList[i]) {
                checkingQR = qrcode
                notifyItemChanged(i)
                return true
            }
        }
        return false
    }
    fun getResult() : List<String> {
        return tagList
    }
        fun addTag(tag: String) {
        tagList.add(tag)
        notifyItemInserted(tagList.size)
    }
}