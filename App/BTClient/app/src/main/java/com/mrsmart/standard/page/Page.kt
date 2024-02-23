package com.mrsmart.standard.page

data class Page(
    // GSON 직렬화 이슈로, PAGE의 전체 속성을 전부 JSON화하지 못하는 것 같습니다.
    // 아래 not using 주석이 처리된 값은 기본값만 들어있음에 유의

    val content: Any?,
    val pageable: Pageable,
    val total: Int,
    val totalElements: Int, // not using
    val last: Boolean, // not using
    val numberOfElements: Int, // not using
    val size: Int, // not using
    val number: Int, // not using
    val sort: Sort,
    val first: Boolean, // not using
    val empty: Boolean // not using
)