package com.liquidskr.btclient

data class Membership (
    var id: Long,
    var code: String,
    var password: String,
    var name: String,
    var partDto: partDto,
    var subPartDtoDto: partDto,
    var mainPartDtoDto: partDto,
    var role: Role,
    var employmentStatus: Status,
)