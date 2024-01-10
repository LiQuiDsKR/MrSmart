package com.mrsmart.standard.standby

data class StandbyParam(
    val rentalSheetId: Long,
    val workerName: String,
    val leaderName: String,
    val timestamp: String,
    val toolList: List<Pair<String, Int>>

)
