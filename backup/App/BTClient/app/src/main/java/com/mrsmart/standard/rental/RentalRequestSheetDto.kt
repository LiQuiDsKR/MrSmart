package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tool.ToolboxDto

data class RentalRequestSheetDto(
    val id: Long,
    val workerDto: MembershipDto,
    val leaderDto: MembershipDto,
    val toolboxDto: ToolboxDto,
    val status: SheetStatus,
    val eventTimestamp: String,
    val toolList: List<RentalRequestToolDto>
)
