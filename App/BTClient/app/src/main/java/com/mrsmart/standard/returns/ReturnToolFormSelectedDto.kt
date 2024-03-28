package com.mrsmart.standard.returns

import com.mrsmart.standard.tool.ToolState

data class ReturnToolFormSelectedDto(
    val rentalToolDtoId: Long,
    val toolDtoId: Long,
    val originTags: String, //added
    var tags:String,
    val originCount:Int, //added
    var goodCount: Int,
    var faultCount: Int,
    var damageCount: Int,
    var lossCount: Int,
    var comment: String,
    var isSelected: Boolean //added
)