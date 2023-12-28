package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tool.ToolboxDto

data class RentalSheetDto(
    val id: Long,
    val workerDto: MembershipDto,
    val leaderDto: MembershipDto,
    val approverDto: MembershipDto,
    val toolboxDto: ToolboxDto,
    val eventTimestamp: String,
    val toolList: List<RentalToolDto>
)
