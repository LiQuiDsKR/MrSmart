package com.mrsmart.standard.membership

data class PartDto(
    val id: Long,
    val name: String,
    val subPartDto: SubPartDto
)