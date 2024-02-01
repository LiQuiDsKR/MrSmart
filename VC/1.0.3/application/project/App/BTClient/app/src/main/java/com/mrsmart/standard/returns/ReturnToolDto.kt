package com.mrsmart.standard.returns

import android.nfc.Tag
import com.mrsmart.standard.rental.RentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto

data class ReturnToolDto(
    val id: Long,
    val rentalSheetDto: RentalSheetDto,
    val rentalToolDto: RentalToolDto,
    val count: Int,
    val goodCount: Int,
    val faultCount: Int,
    val damageCount: Int,
    val lossCount: Int,
    val discardCount: Int,
    val tags: List<Tag>
)
