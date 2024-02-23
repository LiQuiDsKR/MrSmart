package com.liquidskr.btclient

import com.google.gson.Gson
import com.mrsmart.standard.membership.MembershipService

class PCNameService(private val listener: Listener) {
    interface Listener {
        fun onException(type : Constants.ExceptionType, description : String)
        fun onInserted(size : Int, index : Int, total : Int)
    }

    val dbHelper : DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    fun getPCName() : String?{
        try {
            return dbHelper.getDeviceName()
        }catch(e:UninitializedPropertyAccessException){
            listener.onException(Constants.ExceptionType.NO_QUERY_RESULT,"No Results (PCName)")
        }catch(e:Exception){
            listener.onException(Constants.ExceptionType.DATABASE_DEFAULT_EXCEPTION,e.toString())
        }
        return null
    }

    fun insertPCName(name:String){
        try {
            dbHelper.refreshDeviceData(name)
        } catch (e:Exception) {
            listener.onException(Constants.ExceptionType.DATABASE_INSERT_EXCEPTION,e.toString())
        }
    }


}