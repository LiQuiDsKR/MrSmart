package com.liquidskr.btclient

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.RentalRequestSheetDto
import java.lang.reflect.Type

class RentalRequestSheetReadyByMemberReq (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
    val gson = Gson()
    private var currentPage: Int = 0
    private var currentCount: Int = 0

    interface Listener {
        fun onNextPage(pageNum: Int)
        fun onLastPageArrived()
        fun onError(e: Exception)
    }

    fun process(page: Page) {
        try {
            val type: Type = object : TypeToken<List<RentalRequestSheetDto>>() {}.type
            val rentalRequestSheetList: List<MembershipDto> = gson.fromJson(gson.toJson(page.content), type)
            for (sheet in rentalRequestSheetList) {

            }
            currentCount += rentalRequestSheetList.size
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