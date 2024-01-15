package com.mrsmart.standard.tool

data class ToolStateParam(
    val toolid: Long,
    val state: ToolState,
    var count: Int
)
