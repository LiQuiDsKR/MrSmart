package com.mrsmart.standard.sheet.`return`

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.sheet.rental.RentalSheetDto
import com.mrsmart.standard.toolbox.ToolboxDto
import java.time.LocalDateTime

data class ReturnSheetDto(
    val id: Long,
    val rentalSheetDto: RentalSheetDto,
    val workerDto: MembershipDto,
    val leaderDto: MembershipDto,
    val approverDto: MembershipDto,
    val toolboxDto: ToolboxDto,
    val returnTime: LocalDateTime,
    val list: List<ReturnToolDto>
)
