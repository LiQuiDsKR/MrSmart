package com.mrsmart.standard.returns

import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.tool.ToolboxDto
import java.time.LocalDateTime

data class ReturnSheetDto(
    val id: Long,
    val rentalSheetDto: RentalSheetDto,
    val workerDto: Membership,
    val leaderDto: Membership,
    val approverDto: Membership,
    val toolboxDto: ToolboxDto,
    val returnTime: LocalDateTime,
    val list: List<ReturnToolDto>
)
