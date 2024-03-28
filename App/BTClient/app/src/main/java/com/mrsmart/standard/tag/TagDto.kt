package com.mrsmart.standard.tag

import com.mrsmart.standard.sheet.rental.RentalToolDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.toolbox.ToolboxDto

data class TagDto(
    val id: Long,
    val macaddress: String,
    val toolboxDto: ToolboxDto,
    val toolDto: ToolDto,
    val rentalToolDto: RentalToolDto,
    val tagGroup: String
)
