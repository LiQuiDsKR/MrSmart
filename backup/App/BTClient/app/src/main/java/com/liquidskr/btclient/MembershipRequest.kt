    package com.liquidskr.btclient

    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken
    import com.mrsmart.standard.membership.MembershipDto
    import com.mrsmart.standard.page.Page
    import java.lang.reflect.Type

    class MembershipRequest (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
        val gson = Gson()
        var currentPage: Int = 0
        var currentCount: Int = 0

        interface Listener {
            fun onNextPage(pageNum: Int)
            fun onLastPageArrived()
            fun onError(e: Exception)
        }

        fun process(page: Page) {
            try {
                val membershipListType: Type = object : TypeToken<List<MembershipDto>>() {}.type
                val membershipList: List<MembershipDto> = gson.fromJson(gson.toJson(page.content), membershipListType)
                for (member in membershipList) {
                    val id = member.id
                    val code = member.code
                    val password = member.password
                    val name = member.name
                    val part = member.partDto.name
                    val subPart = member.partDto.subPartDto.name
                    val mainPart = member.partDto.subPartDto.mainPartDto.name
                    val role = member.role.toString()
                    val employmentStatus = member.employmentStatus.toString()

                    dbHelper.insertMembershipData(id, code, password, name, part, subPart, mainPart, role, employmentStatus)
                }
                currentCount += membershipList.size
                currentPage++
                if (currentPage >= totalPage) {
                    if(currentCount != totalCount) {
                        listener?.onError(Exception("CountError_diff, current: ${currentCount}, total: ${totalCount}"))
                        return
                    }
                    listener?.onLastPageArrived()
                    return
                }
                listener?.onNextPage(currentPage)
            } catch (e:Exception) {
                listener?.onError(e)
            }

        }
    }