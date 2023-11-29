package com.mrsmart.standard.tool

data class ToolDtoSQLite(
    val id: Long,
    val subGroupDto: String,
    val mainGroupDto: String,
    val code: String,
    val name: String,
    val engName: String,
    val spec: String,
    val unit: String,
    val price: Int,
    val replacementCycle: Int,
    //val buyCode: String
) {
    fun toToolDtoSQLite ():ToolDto {
        val subGroup = SubGroupDto(0, subGroupDto, MainGroupDto(0, mainGroupDto))
        return ToolDto(id, subGroup, code, name, engName, spec, unit ,price, replacementCycle)
    }
}
