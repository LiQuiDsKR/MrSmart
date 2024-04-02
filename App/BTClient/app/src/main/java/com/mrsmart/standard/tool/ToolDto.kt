package com.mrsmart.standard.tool

import com.mrsmart.standard.tool.group.SubGroupDto

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
    fun toToolDtoSQLite (): ToolSQLite {
        val buyCode = buyCode ?: ""
        return ToolSQLite(id, subGroupDto.name, subGroupDto.mainGroupDto.name, code, name, engName, spec, unit, price, replacementCycle, buyCode)
    }
}
