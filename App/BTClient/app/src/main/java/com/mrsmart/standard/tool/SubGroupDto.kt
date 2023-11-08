package com.mrsmart.standard.tool

data class SubGroupDto(
    val id: Long,
    val name: String,
    val mainGroupDto: MainGroupDto
)
