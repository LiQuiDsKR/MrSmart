package com.liquidskr.btclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetApproveFormDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetFormDto
import com.mrsmart.standard.standby.StandbyDto
import com.mrsmart.standard.standby.StandbyParam
import com.mrsmart.standard.sheet.`return`.ReturnSheetFormDto
import com.mrsmart.standard.standby.RentalRequestSheetApproveStandbySheet
import com.mrsmart.standard.standby.RentalRequestSheetFormStandbySheet
import com.mrsmart.standard.standby.ReturnSheetFormStandbySheet


class StandByAdapter(var sheets: List<StandbyDto>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val Rental = 1
    private val Return = 2
    private val RentalRequest = 3
    val gson = Gson()

    override fun getItemViewType(position: Int): Int {
        val sheetPair = sheets[position]
        // Pair의 첫 번째 요소가 "Rental"인 경우 Rental, "Return"인 경우 Return으로 구분
        return when (sheetPair.type) {
            "RENTAL" -> Rental
            "RETURN" -> Return
            "RENTALREQUEST" -> RentalRequest
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
            RentalRequest -> {
                val itemView = inflater.inflate(R.layout.fragment_rentalrequestsheet, parent, false)
                Type3ViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Rental -> {
                if (holder is Type1ViewHolder) {
                    val json = sheets[position].json
                    val detail = sheets[position].detail
                    val standbySheet = gson.fromJson(json, RentalRequestSheetApproveStandbySheet::class.java)
                    val rentalRequestSheetApprove = standbySheet
                    holder.bind(rentalRequestSheetApprove.sheet, detail)
                } else {
                    throw IllegalArgumentException("Invalid view type: ${holder.itemViewType}")
                }
            }
            Return -> {
                if (holder is Type2ViewHolder) {
                    val json = sheets[position].json
                    val detail = sheets[position].detail
                    val standbySheet = gson.fromJson(json, ReturnSheetFormStandbySheet::class.java)
                    val returnSheetForm = standbySheet
                    holder.bind(returnSheetForm.sheet, detail)
                } else {
                    throw IllegalArgumentException("Invalid view type: ${holder.itemViewType}")
                }
            }
            RentalRequest -> {
                if (holder is Type3ViewHolder) {
                    val json = sheets[position].json
                    val detail = sheets[position].detail
                    val standbySheet = gson.fromJson(json, RentalRequestSheetFormStandbySheet::class.java)
                    val rentalRequestSheetForm = standbySheet
                    holder.bind(rentalRequestSheetForm.sheet, detail)
                } else {
                    throw IllegalArgumentException("Invalid view type: ${holder.itemViewType}")
                }
            }
            else -> throw IllegalArgumentException("Invalid view type: ${getItemViewType(position)}")
        }
    }


    override fun getItemCount(): Int = sheets.size

    // ViewHolder 클래스들은 각각의 아이템 타입에 대응합니다.
    inner class Type1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
        var sheetState: TextView = itemView.findViewById(R.id.RentalRequestSheet_sheetState)

        fun bind(item: RentalRequestSheetApproveFormDto, detail: String) {
            val currentRentalRequestSheetApprove = item
            val dbData = gson.fromJson(detail, StandbyParam::class.java)

            workerName.text = dbData.workerName
            leaderName.text = dbData.leaderName
            timeStamp.text = dbData.timestamp.substring(0, 19).replace("T"," ")
            sheetState.text = "신청 완료 (승인 대기)"

            var toolListString = ""
            for (pair in dbData.toolList) {
                val toolName: String = pair.first
                val toolCount: String = pair.second.toString()
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
            toolListTextView.text = toolListString
        }
    }

    inner class Type2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
        var sheetState: TextView = itemView.findViewById(R.id.OutstandingRentalSheet_sheetState)
        fun bind(item: ReturnSheetFormDto, detail: String) {
            val currentReturnSheetFormDto = item
            val dbData = gson.fromJson(detail, StandbyParam::class.java)

            workerName.text = dbData.workerName
            leaderName.text = dbData.leaderName
            timeStamp.text = dbData.timestamp.substring(0, 19).replace("T"," ")
            sheetState.text = "신청 완료 (승인 대기)"
            var toolListString = ""
            for (pair in dbData.toolList) {
                val toolName: String = pair.first
                val toolCount: String = pair.second.toString()
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
            toolListTextView.text = toolListString
        }
    }

    inner class Type3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workerName: TextView = itemView.findViewById(R.id.RentalRequestSheet_WorkerName)
        var leaderName: TextView = itemView.findViewById(R.id.RentalRequestSheet_LeaderName)
        var timeStamp: TextView = itemView.findViewById(R.id.RentalRequestSheet_TimeStamp)
        var toolListTextView: TextView = itemView.findViewById(R.id.ToolListTextView)
        var sheetState: TextView = itemView.findViewById(R.id.RentalRequestSheet_sheetState)

        fun bind(item: RentalRequestSheetFormDto, detail: String) {
            val dbData = gson.fromJson(detail, StandbyParam::class.java)
            workerName.text = dbData.workerName
            leaderName.text = dbData.leaderName
            timeStamp.text = dbData.timestamp.substring(0, 19).replace("T"," ")
            sheetState.text = "미신청 (신청 대기)"
            var toolListString = ""
            for (pair in dbData.toolList) {
                val toolName: String = pair.first
                val toolCount: String = pair.second.toString()
                toolListString = toolListString.plus("$toolName($toolCount)  ")
            }
            toolListTextView.text = toolListString
        }
    }
    fun updateList(newList: List<StandbyDto>) {
        sheets = newList
        notifyDataSetChanged()
    }
}