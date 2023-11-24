package com.mrsmart.standard.rental

import com.mrsmart.standard.tool.ToolDto

data class RentalRequestToolDto(
    val id: Long,
    val rentalRequestSheetDto: RentalRequestSheetDto,
    val toolDto: ToolDto,
    val count: Int,
    val tags: String?
)
