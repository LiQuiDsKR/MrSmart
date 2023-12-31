package com.mrsmart.standard.rental

import com.mrsmart.standard.tool.ToolDto

data class RentalRequestToolDto(
    val id: Long,
    val toolDto: ToolDto,
    val count: Int,
    val Tags: String?
)
