package com.mrsmart.standard.tag

import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.toolbox.ToolboxDto

data class ToolboxToolLabelDto(
    val id: Long,
    val toolboxDto: ToolboxDto,
    val location: String,
    val toolDto: ToolDto,
    val qrcode: String
)
