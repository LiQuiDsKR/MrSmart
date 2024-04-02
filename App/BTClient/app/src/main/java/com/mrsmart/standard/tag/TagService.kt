package com.mrsmart.standard.tag

import android.util.Log
import com.google.gson.Gson
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler

class TagService private constructor() {
    val gson = Gson()

    var inputHandler : InputHandler? = null

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    fun handleResponse(response : Any){
        if (inputHandler==null) {
            Log.e(TAG, "Data receiving object is not set.")
            DialogUtils.showAlertDialog("오류","데이터 수신 객체가 없습니다. : $TAG")
            return
        }

        //if response is list, then make items to TagDto
        if (response is List<*>){
            val tagList = mutableListOf<TagDto>()
            for (item in response){
                val tag = gson.fromJson(gson.toJson(item), TagDto::class.java)
                tagList.add(tag)
            }
            bluetoothManager.endRequest("")
            inputHandler!!.handleTagResponse(tagList)
            return
        }

        Log.d("TagService", "handleResponse: $response")
        inputHandler!!.handleTagResponse(response)
        bluetoothManager.endRequest("")
    }
    companion object {
        private var instance: TagService? = null
        private const val TAG = "TagService"
        fun getInstance(): TagService {
            if (instance == null) {
                instance = TagService()
            }
            return instance!!
        }
    }
}