package com.mrsmart.standard.tool

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputHandler
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page

class TagService private constructor() {
    val gson = Gson()

    var inputHandler : InputHandler? = null

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager.getInstance() }

    fun handleTagInfo(tagDto : TagDto){
        if (inputHandler==null) DialogUtils.showAlertDialog("asdf","asdf")
        inputHandler!!.handleResponse(tagDto)
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