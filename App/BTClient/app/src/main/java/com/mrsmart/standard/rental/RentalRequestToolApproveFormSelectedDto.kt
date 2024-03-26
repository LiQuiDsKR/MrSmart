package com.mrsmart.standard.rental

data class RentalRequestToolApproveFormSelectedDto(
    val id: Long,
    val toolDtoId: Long,
    var count: Int,
    var tags: String,
    var isSelected: Boolean
)