package com.mrsmart.standard.sheet.rentalrequest

data class RentalRequestSheetFormDto(
    val workName: String,
    val workerDtoId: Long,
    val leaderDtoId: Long,
    val toolboxDtoId: Long,
    val toolList: List<RentalRequestToolFormDto>
)
