package com.mrsmart.standard.toolbox

import android.util.Log
import com.google.gson.Gson
import com.liquidskr.btclient.DatabaseHelper

class ToolboxService private constructor() {
    val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    fun getToolbox(): ToolboxDto {
        try {
            val toolboxEntity = dbHelper.getToolbox()
            return toolboxEntity.toToolboxDto()
        } catch (e: UninitializedPropertyAccessException) {
            // Log.e(TAG, "Database not initialized for ID: $id", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            // Log.e(TAG, "Error fetching membership by ID: $id", e)
            throw RuntimeException("Failed to fetch due to an unexpected error.", e)
        }
    }

    fun resetTable() {
        dbHelper.clearToolboxTable()
    }

    fun updateToolbox(toolbox: ToolboxDto) {
        try {
            val id = toolbox.id
            val name = toolbox.name

            dbHelper.updateToolboxData(id, name)
        } catch (e: Exception) {
            Log.e( TAG, "Failed to update toolbox data", e)
            throw Exception("Failed to update toolbox data. Error: ${e.message}", e)
        }
    }

    fun getToolboxById(id: Long): ToolboxDto {
        return dbHelper.getToolboxById(id).toToolboxDto()
    }

    companion object {
        private var instance: ToolboxService? = null
        private const val TAG = "ToolboxService"
        fun getInstance(): ToolboxService {
            if (instance == null) {
                instance = ToolboxService()
            }
            return instance!!
        }
    }
}