package com.mrsmart.standard.membership.part

data class PartDto(
    val id: Long,
    val name: String,
    val subPartDto: SubPartDto
)