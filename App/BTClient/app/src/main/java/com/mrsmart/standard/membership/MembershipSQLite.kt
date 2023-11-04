package com.mrsmart.standard.membership
data class MembershipSQLite(
    val id: Long,
    val name: String,
    val code: String,
    val password: String,
    val part: String,
    val subPart: String,
    val mainPart: String,
    val role: String,
    val employmentStatus: String

    public MembershipSQLite(Membership membership){
        id = membership.getid();
        employmentStatus = membership.getEmploymentStatus().toString();
    }
)