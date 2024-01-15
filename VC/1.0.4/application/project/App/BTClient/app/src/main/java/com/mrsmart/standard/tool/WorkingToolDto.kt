package com.mrsmart.standard.tool

data class WorkingToolDto(
    val id: Long,
    val workingToolboxDto: WorkingToolBoxDto,
    val toolDto: ToolDto,
    val count: Int
)
