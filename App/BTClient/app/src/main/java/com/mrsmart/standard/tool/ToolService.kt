package com.mrsmart.standard.tool

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page

class ToolService private constructor() {
    val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    fun getToolById(id: Long): ToolDto {
        try {
            val toolEntity = dbHelper.getToolById(id)
            return toolEntity.toToolDto()
        } catch (e: UninitializedPropertyAccessException) {
            // Log.e(TAG, "Database not initialized for ID: $id", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            // Log.e(TAG, "Error fetching membership by ID: $id", e)
            throw RuntimeException("Failed to fetch membership by ID: $id due to an unexpected error.", e)
        }
    }
    fun getToolByCode(code: String): ToolDto {
        try {
            val toolEntity = dbHelper.getToolByCode(code)
            return toolEntity.toToolDto()
        } catch (e: UninitializedPropertyAccessException) {
            // Log.e(TAG, "Database not initialized for ID: $id", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            // Log.e(TAG, "Error fetching membership by ID: $id", e)
            throw RuntimeException("Failed to fetch membership by ID: $code due to an unexpected error.", e)
        }
    }

    fun resetTable() {
        dbHelper.clearToolTable()
    }

    fun insertToolByPage(page: Page){
        try {

            val toolListType = object : TypeToken<List<ToolDto>>() {}.type
            val toolList: List<ToolDto> = gson.fromJson(gson.toJson(page.content), toolListType)
            for (tool in toolList) {
                val id = tool.id
                val subGroup = tool.subGroupDto.name
                val mainGroup = tool.subGroupDto.mainGroupDto.name
                val code = tool.code
                val name = tool.name
                val engName = tool.engName
                val spec = tool.spec
                val unit = tool.unit
                val price = tool.price?:0
                val replacementCycle = tool.replacementCycle?:0
                val buyCode = tool.buyCode?:""

                dbHelper.insertToolData(id, mainGroup, subGroup, code, name, engName, spec, unit, price, replacementCycle, buyCode)
                Log.d(TAG, "code : ${code}, name : ${name}, spec : ${spec}, group : ${mainGroup}-${subGroup} inserted.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tool data", e)
            throw Exception("Failed to insert tool data. Error: ${e.message}", e)
        }
    }

    fun upsertToolByPage(page: Page) {
        try {
            val toolListType = object : TypeToken<List<ToolDto>>() {}.type
            val toolList: List<ToolDto> = gson.fromJson(gson.toJson(page.content), toolListType)
            for (tool in toolList) {
                val id = tool.id
                val subGroup = tool.subGroupDto.name
                val mainGroup = tool.subGroupDto.mainGroupDto.name
                val code = tool.code
                val name = tool.name
                val engName = tool.engName
                val spec = tool.spec
                val unit = tool.unit
                val price = tool.price?:0
                val replacementCycle = tool.replacementCycle?:0
                val buyCode = tool.buyCode?:""

                dbHelper.upsertToolData(id, mainGroup, subGroup, code, name, engName, spec, unit, price, replacementCycle, buyCode)
                Log.d(TAG, "code : ${code}, name : ${name}, spec : ${spec}, group : ${mainGroup}-${subGroup} inserted.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tool data", e)
            throw Exception("Failed to insert tool data. Error: ${e.message}", e)
        }
    }

    companion object {
        private var instance: ToolService? = null
        private const val TAG = "ToolService"
        fun getInstance(): ToolService {
            if (instance == null) {
                instance = ToolService()
            }
            return instance!!
        }
    }
}