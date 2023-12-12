package com.mrsmart.standard.tool

data class ToolboxLabelDto(
    val id: Long,
    val toolboxDto: ToolboxDto,
    val location: String,
    val toolDto: ToolDto,
    val qrcode: String
)
