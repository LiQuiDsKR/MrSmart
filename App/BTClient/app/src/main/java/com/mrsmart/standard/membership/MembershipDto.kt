package com.mrsmart.standard.membership

import com.mrsmart.standard.membership.part.PartDto

data class MembershipDto(
    val id: Long,
    val name: String,
    val code: String,
    val password: String,
    val partDto: PartDto,
    val role: Role,
    val employmentStatus: EmploymentStatus
) {
    fun toMembershipSQLite(): MembershipSQLite {
        return MembershipSQLite(
            id,
            name,
            code,
            password,
            partDto.name,
            partDto.subPartDto.name,
            partDto.subPartDto.mainPartDto.name,
            role.name,
            employmentStatus.name
        )
    }
}