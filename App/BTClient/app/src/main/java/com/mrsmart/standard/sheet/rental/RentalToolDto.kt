package com.mrsmart.standard.sheet.rental

import com.mrsmart.standard.tool.ToolDto

data class RentalToolDto(
    var id: Long,
    var toolDto: ToolDto,
    var count: Int,
    var outstandingCount: Int,
    var tags: String // macaddress ','로 구분
)
