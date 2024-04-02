package com.mrsmart.standard.tool.group

data class SubGroupDto(
    val id: Long,
    val name: String,
    val mainGroupDto: MainGroupDto
)
