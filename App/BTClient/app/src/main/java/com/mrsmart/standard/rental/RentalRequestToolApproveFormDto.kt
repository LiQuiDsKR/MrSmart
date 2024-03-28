package com.mrsmart.standard.rental

data class RentalRequestToolApproveFormDto(
    val id: Long,
    val toolDtoId: Long,
    val count: Int,
    val tags: String
)