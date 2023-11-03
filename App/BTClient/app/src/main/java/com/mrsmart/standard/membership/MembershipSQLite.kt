package com.mrsmart.standard.membership
data class MembershipSQLite(
    val id: Long,
    val name: String,
    val code: String,
    val password: String,
    val partDto: String,
    val subPart: String,
    val mainPart: String,
    val role: String,
    val employmentStatus: String
)