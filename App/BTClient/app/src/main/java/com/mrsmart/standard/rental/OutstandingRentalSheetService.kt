package com.mrsmart.standard.rental

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.OutstandingDetailAdapter
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolService

class OutstandingRentalSheetService private constructor() {
    //얘는 Db까지 갈건 없음 ㅇㅇ 걍 임시 데이터 저장소? 느낌
    //val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }

    private val gson =Gson()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var adapter : OutstandingRentalSheetAdapter
    private val viewList : MutableList<OutstandingRentalSheetDto> = mutableListOf()

    fun getList(): List<OutstandingRentalSheetDto> {
        return viewList
    }
    fun getItem(index : Int): OutstandingRentalSheetDto{
        if (index<viewList.size && index>=0)
            return viewList[index]
        else throw ArrayIndexOutOfBoundsException()
    }
    fun add(page : Page){
        try {
            val listType = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
            val sheetList: List<OutstandingRentalSheetDto> = gson.fromJson(gson.toJson(page.content), listType)
            for (sheet in sheetList) {
                val id = sheet.id
                val workerDto = sheet.rentalSheetDto.workerDto
                val leaderDto = sheet.rentalSheetDto.leaderDto
                val toolboxDto = sheet.rentalSheetDto.toolboxDto
                val approverDto = sheet.rentalSheetDto.approverDto
                val eventTimestamp = sheet.rentalSheetDto.eventTimestamp
                val toolList = sheet.rentalSheetDto.toolList
                val status = sheet.outstandingStatus

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
    fun clear(){
        viewList.clear()
        handler.post{
            adapter.updateList(viewList)
        }
    }
    fun setAdapter(adapter:OutstandingRentalSheetAdapter){
        this.adapter=adapter
    }
    companion object {
        private var instance: OutstandingRentalSheetService? = null
        private const val TAG = "outstandingRentalSheetService"
        fun getInstance(): OutstandingRentalSheetService {
            if (instance == null) {
                instance = OutstandingRentalSheetService()
            }
            return instance!!
        }
    }
}