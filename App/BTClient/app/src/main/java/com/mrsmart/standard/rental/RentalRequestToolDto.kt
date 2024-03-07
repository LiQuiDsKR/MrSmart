package com.mrsmart.standard.rental

import com.mrsmart.standard.tool.ToolDto

data class RentalRequestToolDto(
    var id: Long,
    var toolDto: ToolDto,
    var count: Int,
    var tags: String
)
