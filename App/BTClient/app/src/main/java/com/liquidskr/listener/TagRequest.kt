    package com.liquidskr.listener

    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken
    import com.liquidskr.btclient.DatabaseHelper
    import com.mrsmart.standard.page.Page
    import com.mrsmart.standard.tool.TagDto
    import java.lang.reflect.Type

    class TagRequest (private val totalPage: Int, private val totalCount: Int, private val dbHelper: DatabaseHelper, private val listener: Listener) {
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
                val tagListType: Type = object : TypeToken<List<TagDto>>() {}.type
                var tagList: List<TagDto> = gson.fromJson(gson.toJson(page.content), tagListType)
                for (tag in tagList) {
                    val id = tag.id
                    val macaddress = tag.macaddress
                    val tool = tag.toolDto.id
                    val taggroup = tag.tagGroup

                    dbHelper.insertTagData(id, macaddress, tool, taggroup)
                }
                currentCount += tagList.size
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