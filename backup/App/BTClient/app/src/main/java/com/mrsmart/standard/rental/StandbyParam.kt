package com.mrsmart.standard.rental

data class StandbyParam(
    val rentalSheedId: Long,
    val workerName: String,
    val leaderName: String,
    val timestamp: String,
    val toolList: List<Pair<String, Int>>

)
