package com.mrsmart.standard.rental

data class RentalRequestSheetApproveFormDto(
    val id: Long,
    val workerDtoId: Long,
    val leaderDtoId: Long,
    val approverDtoId: Long,
    val toolboxDtoId: Long,
    val toolList: List<RentalRequestToolApproveFormDto>
)
