package com.mrsmart.standard.tool

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.page.Page

class TagService private constructor() {
    val gson = Gson()


    companion object {
        private var instance: TagService? = null
        private const val TAG = "ToolService"
        fun getInstance(): TagService {
            if (instance == null) {
                instance = TagService()
            }
            return instance!!
        }
    }
}