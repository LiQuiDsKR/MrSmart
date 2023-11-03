package com.mrsmart.standard.membership

data class Membership(
    val id: Long,
    val name: String,
    val code: String,
    val password: String,
    val partDto: Part,
    val role: Role,
    val employmentStatus: EmploymentStatus
)