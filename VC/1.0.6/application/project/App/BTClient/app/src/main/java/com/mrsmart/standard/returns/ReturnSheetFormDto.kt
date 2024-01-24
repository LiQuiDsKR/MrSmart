package com.mrsmart.standard.returns

data class ReturnSheetFormDto(
    val rentalSheetDtoId: Long,
    val workerDtoId: Long,
    val approverDtoId: Long,
    val toolboxDtoId: Long,
    val toolList: List<ReturnToolFormDto>
)
