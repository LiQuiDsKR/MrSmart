package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetApprove
import com.mrsmart.standard.rental.RentalRequestToolDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.returns.ReturnSheetFormDto


class StandByAdapter(private var sheets: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val Rental = 1
    private val Return = 2

    override fun getItemViewType(position: Int): Int {
        return when (sheets[position]) {
            is RentalRequestSheetApprove -> Rental
            is ReturnSheetFormDto -> Return
            else -> throw IllegalArgumentException("Invalid data type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Rental -> {
                val itemView = inflater.inflate(R.layout.fragment_rentalrequestsheet, parent, false)
                Type1ViewHolder(itemView)
            }
            Return -> {
                val itemView = inflater.inflate(R.layout.fragment_outstanding_rentalsheet, parent, false)
                Type2ViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Rental -> (holder as Type1ViewHolder).bind(sheets[position] as RentalRequestSheetApprove)
            Return -> (holder as Type2ViewHolder).bind(sheets[position] as OutstandingRentalSheetDto)
            else -> throw IllegalArgumentException("Invalid view type: ${holder.itemViewType}")
        }
    }

    override fun getItemCount(): Int = sheets.size

    // ViewHolder 클래스들은 각각의 아이템 타입에 대응합니다.
    inner class Type1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)

        fun bind(item: RentalRequestSheetApprove) {
            val currentRentalRequestSheetApprove = item
            workerName.text = currentRentalRequestSheetApprove.rentalRequestSheetDto.workerDto.name
            leaderName.text = currentRentalRequestSheetApprove.rentalRequestSheetDto.leaderDto.name
            timeStamp.text = currentRentalRequestSheetApprove.rentalRequestSheetDto.eventTimestamp //LocalDateTime.parse(currentRentalRequestSheet.eventTimestamp).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            var toolListString = ""
            for (tool: RentalRequestToolDto in currentRentalRequestSheetApprove.rentalRequestSheetDto.toolList) {
                val toolName: String = tool.toolDto.name
                val toolCount: String = tool.count.toString()
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
            toolListTextView.text = toolListString
        }
    }

    inner class Type2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
        fun bind(item: OutstandingRentalSheetDto) {
            val currentOutstandingRentalSheet = item
            workerName.text = currentOutstandingRentalSheet.rentalSheetDto.workerDto.name
            leaderName.text = currentOutstandingRentalSheet.rentalSheetDto.leaderDto.name
            timeStamp.text = currentOutstandingRentalSheet.rentalSheetDto.eventTimestamp
            var toolListString = ""
            for (tool: RentalToolDto in currentOutstandingRentalSheet.rentalSheetDto.toolList) {
                val toolName: String = tool.toolDto.name
                val toolCount: String = tool.count.toString()
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
            toolListTextView.text = toolListString
        }
    }
}