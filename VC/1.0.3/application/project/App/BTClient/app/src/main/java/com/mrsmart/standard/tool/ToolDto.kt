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
    val replacementCycle: Int,
    val buyCode: String
) {
    fun toToolDtoSQLite (): ToolDtoSQLite {
        val buyCode = buyCode ?: ""
        return ToolDtoSQLite(id, subGroupDto.name, subGroupDto.mainGroupDto.name, code, name, engName, spec, unit, price, replacementCycle, buyCode)
    }
}
