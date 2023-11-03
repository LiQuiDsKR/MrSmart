package com.mrsmart.standard.tool

data class Tool(
    val id: Long,
    val subGroupDto: SubGroup,
    val code: String,
    val name: String,
    val engName: String,
    val spec: String,
    val unit: String,
    val price: Int,
    val replacementCycle: Int,
    val buyCode: String

)
