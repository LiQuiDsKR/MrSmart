package com.mrsmart.standard.page

data class Pageable(
    val sort: Sort,
    val size: Int,
    val page: Int
)
