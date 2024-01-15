package com.mrsmart.standard.tool

import com.mrsmart.standard.membership.MembershipDto

data class ToolboxDto(
    val id: Long,
    val name: String,
    val managerDto: MembershipDto,
    val systemOperability: Boolean
)