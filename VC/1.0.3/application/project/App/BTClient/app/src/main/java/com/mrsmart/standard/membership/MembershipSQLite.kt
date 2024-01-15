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
) {
    fun toMembership(): MembershipDto { // MembershipSQLite 는 part들의 이름만 잘라 가져온 객체이므로 part, subPart, mainPart의 id는 가져올 수 없음
        val part = PartDto(0, part, SubPartDto(0, subPart, MainPartDto(0, mainPart, "", "", ""), "", "", ""))
        return MembershipDto(id, name, code, password, part, Role.valueOf(role), EmploymentStatus.valueOf(employmentStatus))
    }
}