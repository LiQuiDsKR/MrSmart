package com.mrsmart.standard.tag

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.tool.ToolService
import com.mrsmart.standard.toolbox.ToolboxService

class ToolboxToolLabelService private constructor() {
    val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    var inputHandler : InputHandler? = null

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    fun getToolboxToolLabelTextByToolId(toolId: Long): String {
        try {
            val labelText = dbHelper.getTBTByToolId(toolId)
            if (labelText != "" && labelText != null) {
                return labelText
            } else {
                throw IllegalStateException("No ToolboxToolLabel found for tool ID: $toolId")
            }
        } catch (e: UninitializedPropertyAccessException) {
            // Log.e(TAG, "Database not initialized for ID: $id", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: IllegalStateException) {
            // Log.e(TAG, "No ToolboxToolLabel found for ID: $id", e)
            throw IllegalStateException("No ToolboxToolLabel found for tool ID: $toolId", e)
        } catch (e: Exception) {
            // Log.e(TAG, "Error fetching membership by ID: $id", e)
            throw RuntimeException("Failed to fetch membership by ID: $toolId due to an unexpected error.", e)
        }
    }

    fun resetTable() {
        dbHelper.clearTBTTable()
    }

    fun insertToolboxToolLabelByPage(page: Page){
        try {

            val labelListType = object : TypeToken<List<ToolboxToolLabelDto>>() {}.type
            val labelList: List<ToolboxToolLabelDto> = gson.fromJson(gson.toJson(page.content), labelListType)
            for (label in labelList) {
                val id = label.id
                val toolboxId = label.toolboxDto.id
                val location = label.location
                val toolId = label.toolDto.id
                val qrcode = label.qrcode

                dbHelper.insertTBTData(id, toolboxId, location, toolId, qrcode)
                Log.d(TAG, "qrcode : ${qrcode}, toolId : ${toolId}, toolboxId : ${toolboxId}, location : ${location}, id : ${id}, inserted.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tool data", e)
            throw Exception("Failed to insert tool data. Error: ${e.message}", e)
        }
    }

    fun isToolboxToolLabelExistByToolId(toolId: Long): Boolean {
        return dbHelper.isTBTExistByToolId(toolId)
    }
    fun isToolboxToolLabelExist(qrCode : String) : Boolean {
        return dbHelper.isTBTExist(qrCode)
    }
    fun upsertToolboxToolLabelByPage(page: Page) {
        try {
            val labelListType = object : TypeToken<List<ToolboxToolLabelDto>>() {}.type
            val labelList: List<ToolboxToolLabelDto> =
                gson.fromJson(gson.toJson(page.content), labelListType)
            for (label in labelList) {
                val id = label.id
                val toolboxId = label.toolboxDto.id
                val location = label.location
                val toolId = label.toolDto.id
                val qrcode = label.qrcode

                dbHelper.upsertTBTData(id, toolboxId, location, toolId, qrcode)
                Log.d(
                    TAG,
                    "qrcode : ${qrcode}, toolId : ${toolId}, toolboxId : ${toolboxId}, location : ${location}, id : ${id}, inserted."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tool data", e)
            throw Exception("Failed to insert tool data. Error: ${e.message}", e)
        }
    }

    fun update(){
        dbHelper.updateToolbox
    }

    fun handleResponse(response : Any){
        if (inputHandler==null) {
            Log.e(TAG, "Data receiving object is not set.")
            DialogUtils.showAlertDialog("오류","데이터 수신 객체가 없습니다. : $TAG")
            return
        }

        //if response is list, then make items to ToolboxToolLabelDto
        //아마 그럴일 없음 ㅇㅇ
        if (response is List<*>){
            val labelList = mutableListOf<ToolboxToolLabelDto>()
            for (item in response){
                val label = gson.fromJson(gson.toJson(item), ToolboxToolLabelDto::class.java)
                labelList.add(label)
            }
            bluetoothManager.endRequest("")
            inputHandler!!.handleToolboxToolLabelResponse(labelList)
            return
        }

        Log.d(TAG, "handleResponse: $response")
        inputHandler!!.handleToolboxToolLabelResponse(response)
        bluetoothManager.endRequest("")
    }

    companion object {
        private var instance: ToolboxToolLabelService? = null
        private const val TAG = "ToolboxToolLabelService"
        fun getInstance(): ToolboxToolLabelService {
            if (instance == null) {
                instance = ToolboxToolLabelService()
            }
            return instance!!
        }
    }
}