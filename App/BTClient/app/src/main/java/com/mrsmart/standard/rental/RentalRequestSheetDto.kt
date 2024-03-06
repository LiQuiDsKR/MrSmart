package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.toolbox.ToolboxDto

data class RentalRequestSheetDto(
    var id: Long,
    var workerDto: MembershipDto,
    var leaderDto: MembershipDto,
    var toolboxDto: ToolboxDto,
    var status: SheetState,
    var eventTimestamp: String,
    var toolList: List<RentalRequestToolDto>
)
