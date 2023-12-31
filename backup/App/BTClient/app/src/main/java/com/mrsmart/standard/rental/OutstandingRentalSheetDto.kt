package com.mrsmart.standard.rental

data class OutstandingRentalSheetDto(
    val id: Long,
    val rentalSheetDto: RentalSheetDto,
    val totalCount: Int,
    val totalOutstandingCount: Int,
    val outstandingStatus: OutstandingStatus
)
