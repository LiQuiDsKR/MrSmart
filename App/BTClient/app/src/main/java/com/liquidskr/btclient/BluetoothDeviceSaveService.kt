package com.liquidskr.btclient

import android.util.Log
import com.google.gson.Gson

class BluetoothDeviceSaveService private constructor() {
    val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    fun getPCName(): String {
        try {
            return dbHelper.getDeviceName()
        } catch (e: UninitializedPropertyAccessException) {
            Log.e(TAG, "Database not initialized.", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching PC name.", e)
            throw RuntimeException("Failed to fetch PC name due to an unexpected error.", e)
        }
    }

    fun insertPCName(name: String) {
        try {
            dbHelper.refreshDeviceData(name)
            Log.d(TAG, "PC Name inserted successfully: $name")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert PC Name: $name", e)
            throw Exception("Failed to insert PC Name: $name. Error: ${e.message}", e)
        }
    }

    companion object {
        private var instance: BluetoothDeviceSaveService? = null
        private const val TAG = "PCNameService"
        fun getInstance(): BluetoothDeviceSaveService {
            if (instance == null) {
                instance = BluetoothDeviceSaveService()
            }
            return instance!!
        }
    }
}