package com.mrsmart.standard.tool

import com.mrsmart.standard.membership.Membership

data class ToolboxDto(
    val id: Long,
    val name: String,
    val managerDto: Membership,
    val systemOperability: Boolean
)