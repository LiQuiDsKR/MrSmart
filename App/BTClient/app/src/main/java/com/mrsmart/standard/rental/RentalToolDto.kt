package com.mrsmart.standard.rental

import com.mrsmart.standard.tool.ToolDto

data class RentalToolDto(
    val id: Long,
    val toolDto: ToolDto,
    val count: Int,
    val outstandingCount: Int,
    val Tags: String // macaddress ','로 구분
)
