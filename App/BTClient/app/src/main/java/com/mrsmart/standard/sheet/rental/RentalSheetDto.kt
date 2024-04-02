package com.mrsmart.standard.sheet.rental

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.toolbox.ToolboxDto

data class RentalSheetDto(
    var id: Long,
    var workerDto: MembershipDto,
    var leaderDto: MembershipDto,
    var approverDto: MembershipDto,
    var toolboxDto: ToolboxDto,
    var eventTimestamp: String,
    var toolList: List<RentalToolDto>
)
