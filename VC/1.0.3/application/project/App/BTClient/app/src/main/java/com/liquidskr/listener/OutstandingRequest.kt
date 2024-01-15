    package com.liquidskr.listener

    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken
    import com.liquidskr.btclient.DatabaseHelper
    import com.mrsmart.standard.membership.MembershipDto
    import com.mrsmart.standard.page.Page
    import com.mrsmart.standard.rental.OutstandingRentalSheetDto
    import java.lang.reflect.Type

    class OutstandingRequest (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
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
                val sheetListType: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                val sheetList: List<OutstandingRentalSheetDto> = gson.fromJson(gson.toJson(page.content), sheetListType)
                for (sheet in sheetList) {
                    val id = sheet.id
                    val rentalSheet = gson.toJson(sheet.rentalSheetDto)
                    val totalCount = sheet.totalCount
                    val totalOutstandingCount = sheet.totalOutstandingCount
                    val status = sheet.outstandingStatus.name
                    val json = gson.toJson(sheet)

                    dbHelper.insertOutstandingData(id, rentalSheet, totalCount, totalOutstandingCount, status, json)
                }
                currentCount += sheetList.size
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