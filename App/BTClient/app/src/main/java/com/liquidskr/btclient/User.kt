package com.liquidskr.btclient

data class User (
    var id: Long,
    var code: String,
    var password: String,
    var name: String,
    var partDto: String,
    var subPartDtoDto: String,
    var mainPartDtoDto: String,
    var role: String,
    var employmentStatus: String,
)