package com.mrsmart.standard.rental

import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.tool.ToolboxDto
import java.time.LocalDateTime

data class RentalSheetDto(
    val id: Long,
    val workerDto: Membership,
    val leaderDto: Membership,
    val approverDto: Membership,
    val toolboxDto: ToolboxDto,
    val eventTimeStamp: LocalDateTime,
    val list: List<RentalToolDto>
)
