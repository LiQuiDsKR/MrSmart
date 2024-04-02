package com.mrsmart.standard.sheet.`return`

data class ReturnToolFormDto(
    val rentalToolDtoId: Long,
    val toolDtoId: Long,
    val tags: String,
    val goodCount: Int,
    val faultCount: Int,
    val damageCount: Int,
    val lossCount: Int,
    val comment: String
)