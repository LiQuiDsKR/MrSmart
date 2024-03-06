package com.mrsmart.standard.toolbox

import com.mrsmart.standard.membership.MembershipDto

data class ToolboxDto(
    val id: Long,
    val name: String,
    //val managerDto: MembershipDto, //얘는 나중에 그래도 어느 실 manager인지 판단해서 자동 로그인할 수 있게 하면 좋을 것 같은데
    //val systemOperability: Boolean
)