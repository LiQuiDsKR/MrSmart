package com.mrsmart.standard.toolbox

data class ToolboxSQLite(
    val id: Long,
    val name: String
) {
    fun toToolboxDto(): ToolboxDto {
        return ToolboxDto(id, name)
    }
}