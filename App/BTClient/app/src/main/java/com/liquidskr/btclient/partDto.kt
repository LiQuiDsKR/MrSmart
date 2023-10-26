package com.liquidskr.btclient

data class partDto (
    var id: Long,
    var name: String,
    var subPartDto: subPartDto
)