package com.mrsmart.standard.tool

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
            DialogUtils.showAlertDialog("오류","데이터 전송 후 받는 객체가 없습니다. : Tagservice")
            return
        }
        inputHandler!!.handleResponse(response)
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