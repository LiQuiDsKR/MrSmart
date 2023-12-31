    package com.liquidskr.btclient

    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken
    import com.mrsmart.standard.page.Page
    import com.mrsmart.standard.tool.ToolDto
    import java.lang.reflect.Type

    class ToolRequest (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
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
                val toolListType: Type = object : TypeToken<List<ToolDto>>() {}.type
                val toolList: List<ToolDto> = gson.fromJson(gson.toJson(page.content), toolListType)
                for (tool in toolList) {
                    val id = tool.id
                    val mainGroup = tool.subGroupDto.mainGroupDto.name
                    val subGroup = tool.subGroupDto.name
                    val code = tool.code
                    val krName = tool.name
                    val engName = tool.engName
                    val spec = tool.spec
                    val unit = tool.unit
                    val price = tool.price
                    val replacementCycle = tool.replacementCycle
                    val buyCode = "" // 실데이터 생기면 넣어야함

                    dbHelper.insertToolData(id, mainGroup, subGroup, code, krName, engName, spec, unit, price, replacementCycle, buyCode)
                }
                currentCount += toolList.size
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