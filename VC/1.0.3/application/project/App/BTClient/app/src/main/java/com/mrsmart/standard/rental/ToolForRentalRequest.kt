package com.mrsmart.standard.rental

data class ToolForRentalRequest(
    val size: Int,
    val index: Int,
    val toolboxId: Long,
    val name: String,
    val subGroupId: List<Long>
)
