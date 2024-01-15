package com.mrsmart.standard.page

data class Page(
    val content: Any?,
    val pageable: Pageable,
    val total: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val empty: Boolean
)