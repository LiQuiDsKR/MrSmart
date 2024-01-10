package com.mrsmart.standard.returns

import com.mrsmart.standard.tool.ToolState

data class ReturnToolFormDto(
    val rentalToolDtoId: Long,
    val toolDtoId: Long,
    val count: Int,
    val status: ToolState,
    val Tags: String
)
