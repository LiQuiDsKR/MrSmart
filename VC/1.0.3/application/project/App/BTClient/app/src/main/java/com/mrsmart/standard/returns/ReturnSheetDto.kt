package com.mrsmart.standard.returns

import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.tool.ToolboxDto
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
