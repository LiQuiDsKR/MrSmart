package com.mrsmart.standard.sheet.rentalrequest

data class RentalRequestToolApproveFormDto(
    val id: Long,
    val toolDtoId: Long,
    val count: Int,
    val tags: String
)