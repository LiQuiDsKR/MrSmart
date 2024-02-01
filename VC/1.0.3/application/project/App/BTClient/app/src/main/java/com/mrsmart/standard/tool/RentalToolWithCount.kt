package com.mrsmart.standard.tool

import com.mrsmart.standard.rental.RentalToolDto

data class RentalToolWithCount(
    var rentalTool: RentalToolDto,
    var count: Int
)
