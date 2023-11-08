package com.mrsmart.standard.tool

data class ToolDto(
    val id: Long,
    val subGroupDto: SubGroupDto,
    val code: String,
    val name: String,
    val engName: String,
    val spec: String,
    val unit: String,
    val price: Int,
    val replacementCycle: Int
    // val buyCode: String
)
