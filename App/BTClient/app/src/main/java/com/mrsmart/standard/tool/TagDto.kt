package com.mrsmart.standard.tool

import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.toolbox.ToolboxDto

data class TagDto(
    val id: Long,
    val macaddress: String,
    val toolboxDto: ToolboxDto,
    val toolDto: ToolDto,
    val rentalToolDto: RentalToolDto,
    val tagGroup: String
)
