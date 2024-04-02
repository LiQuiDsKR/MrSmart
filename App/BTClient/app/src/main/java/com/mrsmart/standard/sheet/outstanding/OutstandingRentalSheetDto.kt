package com.mrsmart.standard.sheet.outstanding

import com.mrsmart.standard.sheet.rental.RentalSheetDto

data class OutstandingRentalSheetDto(
    var id: Long,
    var rentalSheetDto: RentalSheetDto,
    var totalCount: Int,
    var totalOutstandingCount: Int,
    var outstandingStatus: OutstandingState
)
