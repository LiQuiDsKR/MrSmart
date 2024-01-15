package com.liquidskr.btclient

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToolRegisterTagDetailAdapter(var qrcodes: List<String>) :
    RecyclerView.Adapter<ToolRegisterTagDetailAdapter.ToolViewHolder>() {
    var checkingQR = "default"

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
        val currentQR = qrcodes[position]
        holder.qrcode = currentQR
        holder.qrDisplay.text = currentQR
        holder.qrDelete.setOnClickListener {
            Log.d("regiAdapter",qrcodes.toString())
            val updatedList = qrcodes.toMutableList()
            updatedList.removeAt(holder.adapterPosition)
            qrcodes = updatedList
            notifyDataSetChanged()
        }
        if (checkingQR == holder.qrcode) {
            // 현재 항목이 qrcode와 일치하면 배경색 변경
            holder.itemView.setBackgroundColor(Color.GREEN)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }, 300)
        }
        checkingQR = "default"
    }
    override fun getItemCount(): Int {
        return qrcodes.size
    }
    fun updateList(newList: List<String>) {
        qrcodes = newList
        notifyDataSetChanged()
    }
    private fun fixCode(input: String): String {
        val typoMap = mapOf(
            'ㅁ' to 'A',
            'ㅠ' to 'B',
            'ㅊ' to 'C',
            'ㅇ' to 'D',
            'ㄷ' to 'E',
            'ㄹ' to 'F',
            'ㅎ' to 'G',
            'ㅗ' to 'H',
            'ㅑ' to 'I',
            'ㅓ' to 'J',
            'ㅏ' to 'K',
            'ㅣ' to 'L',
        )
        val correctedText = StringBuilder()
        for (char in input) {
            val correctedChar = typoMap[char] ?: char
            correctedText.append(correctedChar)
        }
        return correctedText.toString()
    }
    fun qrCheck(qrcode: String) {
        for (i in qrcodes.indices) {
            if (qrcode == qrcodes[i]) {
                checkingQR = qrcode
                notifyItemChanged(i)
            }
        }
    }
}