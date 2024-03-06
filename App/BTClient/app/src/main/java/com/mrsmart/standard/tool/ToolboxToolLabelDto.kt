package com.mrsmart.standard.tool

import com.mrsmart.standard.toolbox.ToolboxDto

data class ToolboxToolLabelDto(
    val id: Long,
    val toolboxDto: ToolboxDto,
    val location: String,
    val toolDto: ToolDto,
    val qrcode: String
)
