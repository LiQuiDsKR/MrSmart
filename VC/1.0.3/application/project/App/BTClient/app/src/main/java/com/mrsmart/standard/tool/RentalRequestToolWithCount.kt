package com.mrsmart.standard.tool

import com.mrsmart.standard.rental.RentalRequestToolDto

data class RentalRequestToolWithCount(
    var rentalRequestTool: RentalRequestToolDto,
    var count: Int
)
