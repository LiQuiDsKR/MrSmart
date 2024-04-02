package com.mrsmart.standard.sheet.rentalrequest

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolService

class RentalRequestSheetService private constructor() {
    //얘는 Db까지 갈건 없음 ㅇㅇ 걍 임시 데이터 저장소? 느낌
    //val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }

    private val gson =Gson()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var adapter : RentalRequestSheetAdapter
    private val viewList : MutableList<RentalRequestSheetDto> = mutableListOf()
    var currentSheetId : Long = -1

    fun getList(): List<RentalRequestSheetDto> {
        return viewList
    }
    fun getItem(index : Int): RentalRequestSheetDto {
        if (index<viewList.size && index>=0)
            return viewList[index]
        else throw ArrayIndexOutOfBoundsException()
    }
    fun add(page : Page){
        try {
            val listType = object : TypeToken<List<RentalRequestSheetDto>>() {}.type
            val sheetList: List<RentalRequestSheetDto> = gson.fromJson(gson.toJson(page.content), listType)
            for (sheet in sheetList) {
                val id = sheet.id
                val workerDto = sheet.workerDto
                val leaderDto = sheet.leaderDto
                val toolboxDto = sheet.toolboxDto
                val status = sheet.status
                val eventTimestamp = sheet.eventTimestamp
                val toolList = sheet.toolList

                viewList.add(sheet)
            }
            handler.post{
                adapter.insertList(sheetList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encode rentalRequestSheet data", e)
            throw Exception("Failed to encode rentalRequestSheet data. Error: ${e.message}", e)
        }
    }

    fun deleteItem(){
        val index = viewList.indexOfFirst { it.id == currentSheetId }
        if (index>=0){
            viewList.removeAt(index)
            handler.post{
                adapter.notifyItemRemoved(index)
            }
        }
    }

    fun clear(){
        viewList.clear()
        handler.post{
            adapter.updateList(viewList)
        }
    }
    fun setAdapter(adapter:RentalRequestSheetAdapter){
        this.adapter=adapter
    }
    companion object {
        private var instance: RentalRequestSheetService? = null
        private const val TAG = "rentalRequestSheetService"
        fun getInstance(): RentalRequestSheetService {
            if (instance == null) {
                instance = RentalRequestSheetService()
            }
            return instance!!
        }
    }
}