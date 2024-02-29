package com.liquidskr.btclient

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.tool.TagAndToolboxToolLabelDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolboxCompressDto
import com.mrsmart.standard.tool.ToolboxToolLabelDto
import com.liquidskr.btclient.Constants.BluetoothMessageType.*
import java.lang.reflect.Type

class BluetoothManager (private val handler : Handler){

    private val gson = Gson()


    interface Listener {
        fun onDisconnected()
        fun onRequestStarted()
        fun onRequestProcessed(context : String, processedAmount : Int , totalAmount : Int)
        fun onRequestEnded()
        fun onRequestFailed(message : String)
        fun onException(message : String)
    }
    var listener : Listener? = null

    private val bluetoothCommunicationHandlerListener = object:BluetoothCommunicationHandler.Listener{
        override fun onConnected() {
            Log.d("bluetooth","Connected")
            handler.post{
                listener?.onRequestEnded()
            }
        }

        override fun onDisconnected() {
            Log.d("bluetooth","Mngr : Disconnected!")
        }

        override fun onReconnectStarted() {
            handler.post {
                listener?.onDisconnected()
            }
        }

        override fun onReconnectFailed() {
            handler.post{
                listener?.onRequestFailed("abcsdfwlo")
            }
        }

        override fun onDataArrived(data: String) {
            Log.d("bluetooth","final : $data")
            processData(data)
        }

        override fun onDataSent(data: String) {
            Log.d("bluetooth","good send")
        }

        override fun onException(type: Constants.ExceptionType, description: String) {
            Log.d("bluetooth","exception final : [${type.name}] : [${description}]")
            when (type){
                Constants.ExceptionType.BLUETOOTH_NO_PAIRED_DEVICE ->{
                    handler.post {
                        listener?.onDisconnected()
                    }
                }
                else -> {}
            }
        }

    }
    private val bluetoothCommunicationHandler : BluetoothCommunicationHandler = BluetoothCommunicationHandler(bluetoothCommunicationHandlerListener)

    private val membershipServiceListener = object: MembershipService.Listener{
        override fun onException(type: Constants.ExceptionType, description: String) {
            Log.d("membership","exception final : [${type.name}] : [${description}]")
        }

        override fun onInserted(size: Int, index: Int, total: Int) {
            val pageSize = Constants.MEMBERSHIP_PAGE_SIZE.coerceAtMost(total)

            Log.d("membership","$index / ${total/pageSize} pages inserted. (size : ${size})")

            loadingPageIndex+=1

            if (loadingPageIndex>total/pageSize) {
                Log.d("membership", "membership insert complete (size : ${total})")
                return
            }

            val message : String =
                MEMBERSHIP_ALL.toString() +
                        ",{\"size\":${pageSize},\"page\":${loadingPageIndex}}"

            handler.post{
                listener?.onRequestProcessed(MEMBERSHIP_ALL.processMessage,index,total/pageSize)
            }

            bluetoothCommunicationHandler.send(message.trim())
        }
    }
    private val membershipService = MembershipService(membershipServiceListener)

    private var loadingPageIndex : Int = -1 // -1 : not loading , 0~ : loading index


    fun send(type:Constants.BluetoothMessageType,data:String){

        handler.post{
            listener?.onRequestStarted()
        }

        when(type){
            MEMBERSHIP_ALL_COUNT ->{
                bluetoothCommunicationHandler.send(type.toString())
            }
            TOOL_ALL_COUNT ->{
                bluetoothCommunicationHandler.send(type.toString())
            }
            else -> {Log.d("bluetooth","존재하지 않는 데이터 타입입니다")}
        }
    }
    
    fun processData(data:String) {
        val (typeStr, jsonStr) = data.split(',', limit = 2)
        when(typeStr){
            MEMBERSHIP_ALL.name -> {
                //response
                val membershipPage = gson.fromJson(jsonStr, Page::class.java)

                membershipService.insertMembershipByPage(membershipPage)
            }

            MEMBERSHIP_ALL_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //event
                handler.post{
                    listener?.onRequestProcessed(MEMBERSHIP_ALL_COUNT.processMessage,1,1)
                }

                val size = Constants.MEMBERSHIP_PAGE_SIZE.coerceAtMost(total)

                loadingPageIndex=0

                val message : String =
                    MEMBERSHIP_ALL.toString() +
                            ",{\"size\":${size},\"page\":${loadingPageIndex}}"

                bluetoothCommunicationHandler.send(message.trim())
            }

            TOOL_ALL.name -> {
                val listType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            TOOL_ALL_COUNT.name -> {
                val listType: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }

            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            RETURN_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX.name -> {
                val listType: Type = object : TypeToken<List<RentalRequestSheetDto>>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP.name -> {
                val type: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_APPROVE.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RETURN_SHEET_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_APPROVE.name -> {
                val pageType: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_TOOL_LABEL_FORM.name -> {
                val pageType: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX.name -> {
                val listType: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                TODO("not implemented yet")
            }
            RETURN_SHEET_REQUEST.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TAG_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_TOOL_LABEL.name -> {
                val type: Type = object : TypeToken<ToolboxToolLabelDto>() {}.type
                TODO("not implemented yet")
            }
            TAG_LIST.name -> {
                val type: Type = object : TypeToken<List<String>>() {}.type
                TODO("not implemented yet")
            }
            TAG_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            TAG_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_TOOL_LABEL_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_TOOL_LABEL_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TAG_GROUP.name -> {
                val type: Type = object : TypeToken<TagDto>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_BY_TAG.name -> {
                val type: Type = object : TypeToken<OutstandingRentalSheetDto>() {}.type
                TODO("not implemented yet")
            }
            TAG.name -> {
                val type: Type = object : TypeToken<TagDto>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_FORM_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_APPROVE_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RETURN_SHEET_FORM_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_CANCEL.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            RENTAL_REQUEST_SHEET_APPLY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TAG_AND_TOOLBOX_TOOL_LABEL_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TAG_AND_TOOLBOX_TOOL_LABEL.name -> {
                val type: Type = object : TypeToken<TagAndToolboxToolLabelDto>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_ALL.name -> {
                val type: Type = object : TypeToken<List<ToolboxCompressDto>>() {}.type
                TODO("not implemented yet")
            }

            // ###################
            TEST.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            HI.name -> {
                Log.d("bluetooth",jsonStr)
            }
        }
    }

    fun disconnect(){
        bluetoothCommunicationHandler.disconnect()
    }

    fun connect() {
        bluetoothCommunicationHandler.connect()
    }
}