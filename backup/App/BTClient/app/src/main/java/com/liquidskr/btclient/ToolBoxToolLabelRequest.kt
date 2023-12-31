    package com.liquidskr.btclient

    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken
    import com.mrsmart.standard.membership.MembershipDto
    import com.mrsmart.standard.page.Page
    import com.mrsmart.standard.tool.ToolboxToolLabelDto
    import java.lang.reflect.Type

    class ToolBoxToolLabelRequest (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
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
                val toolBoxToolLabelListType: Type = object : TypeToken<List<ToolboxToolLabelDto>>() {}.type
                var toolBoxToolLabelList: List<ToolboxToolLabelDto> = gson.fromJson(gson.toJson(page.content), toolBoxToolLabelListType)
                for (tbt in toolBoxToolLabelList) {
                    val id = tbt.id
                    val toolbox = tbt.toolboxDto.id
                    val location = tbt.location
                    val tool = tbt.toolDto.id
                    val qrcode = tbt.qrcode

                    dbHelper.insertTBTData(id, toolbox, location, tool, qrcode)
                }
                currentCount += toolBoxToolLabelList.size
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