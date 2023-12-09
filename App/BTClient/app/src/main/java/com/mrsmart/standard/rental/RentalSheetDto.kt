package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.tool.ToolboxDto

data class RentalSheetDto(
    val id: Long,
    val workerDto: Membership,
    val leaderDto: Membership,
    val approverDto: Membership,
    val toolboxDto: ToolboxDto,
    val eventTimestamp: String,
    val toolList: List<RentalToolDto>
)
