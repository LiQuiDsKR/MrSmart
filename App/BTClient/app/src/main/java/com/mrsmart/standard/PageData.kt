package com.mrsmart.standard

import com.mrsmart.standard.membership.Membership

data class PageData(
    val content: Any?,
    val pageable: Pageable,
    val totalPage: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val empty: Boolean
)