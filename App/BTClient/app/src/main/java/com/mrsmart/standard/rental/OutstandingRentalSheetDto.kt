package com.mrsmart.standard.rental

data class OutstandingRentalSheetDto(
    var id: Long,
    var rentalSheetDto: RentalSheetDto,
    var totalCount: Int,
    var totalOutstandingCount: Int,
    var outstandingStatus: OutstandingStatus
)
