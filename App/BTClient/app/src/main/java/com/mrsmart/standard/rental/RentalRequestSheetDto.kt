package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.tool.ToolboxDto

data class RentalRequestSheetDto(
    val id: Long,
    val workerDto: Membership,
    val leaderDto: Membership,
    val toolboxDto: ToolboxDto,
    val status: SheetStatus,
    val eventTimestamp: String,
    val toolList: List<RentalRequestToolDto>
)
