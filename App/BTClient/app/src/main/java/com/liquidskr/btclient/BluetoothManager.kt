package com.liquidskr.btclient

import android.bluetooth.BluetoothDevice
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
import com.mrsmart.standard.tool.ToolboxToolLabelDto
import com.liquidskr.btclient.Constants.BluetoothMessageType.*
import com.mrsmart.standard.rental.RentalRequestSheetService
import com.mrsmart.standard.tool.ToolService
import com.mrsmart.standard.toolbox.ToolboxDto
import com.mrsmart.standard.toolbox.ToolboxService
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


    private lateinit var bluetoothDevice : BluetoothDevice
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

    private val membershipService = MembershipService.getInstance()
    private val toolService = ToolService.getInstance()
    private val rentalRequestSheetService = RentalRequestSheetService.getInstance()

    private var loadingPageIndex : Int = -1 // -1 : not loading , 0~ : loading index
    private var reloadFlag : Boolean = false // false : 안끊김 (insert) , true : 끊겼었음. 재송신중 (upsert)
    private var lastSendedMessageType : Constants.BluetoothMessageType = NULL
    private var lastSendedMessageData : String = ""


    fun send(type:Constants.BluetoothMessageType,data:String){

        if (type == TOOLBOX_ALL){
            Log.d("bluetooth","Toolbox-all 넘어감")
        } else {
            handler.post {
                listener?.onRequestStarted()
            }
        }

        //cache
        lastSendedMessageType = type
        lastSendedMessageData = data

        when(type){
            NULL -> {
                Log.d("bluetooth","NULL")
            }
            MEMBERSHIP_ALL ->{
                bluetoothCommunicationHandler.send("$type,$data")
            }
            MEMBERSHIP_ALL_COUNT ->{
                bluetoothCommunicationHandler.send(type.toString())
            }
            TOOL_ALL ->{
                bluetoothCommunicationHandler.send("$type,$data")
            }
            TOOL_ALL_COUNT ->{
                bluetoothCommunicationHandler.send(type.toString())
            }
            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX ->{
                bluetoothCommunicationHandler.send("$type,$data")
            }
            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT ->{
                bluetoothCommunicationHandler.send("$type,$data")
            }
            TOOLBOX_ALL->{
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

                //database update
                if (reloadFlag) {
                    membershipService.upsertMembershipByPage(membershipPage)
                    reloadFlag=false
                } else {
                    membershipService.insertMembershipByPage(membershipPage)
                }

                //preprocess - parse
                //val size = membershipPage.size  ** Page 객체가 JSON으로 불러오는 데이터 포맷과 정확히 호환되지 않고 있습니다
                val index = membershipPage.pageable.page
                val total = membershipPage.total

                //preprocess - logic
                val pageSize = Constants.MEMBERSHIP_PAGE_SIZE.coerceAtMost(total)
                Log.d("membership","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1


                //event
                handler.post{
                    listener?.onRequestProcessed(MEMBERSHIP_ALL.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - membership load finished
                    Log.d("membership", "membership insert complete (size : ${total})")
                    val type = TOOL_ALL_COUNT
                    val data = ""
                    send(type, data)

                } else{

                    //send 2 - membership load not finished
                    val type = MEMBERSHIP_ALL
                    val data = "{\"size\":${pageSize},\"page\":${loadingPageIndex}}"
                    send(type, data)

                }

            }

            MEMBERSHIP_ALL_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //database update
                membershipService.resetTable()

                //event
                handler.post{
                    listener?.onRequestProcessed(MEMBERSHIP_ALL_COUNT.processMessage,1,1)
                }

                //postprocess - logic
                val size = Constants.MEMBERSHIP_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val type = MEMBERSHIP_ALL
                val data = "{\"size\":${size},\"page\":${loadingPageIndex}}"
                send(type,data)
            }

            TOOL_ALL.name -> {
                //response
                val toolPage = gson.fromJson(jsonStr, Page::class.java)

                //database update
                if (reloadFlag){
                    toolService.upsertToolByPage(toolPage)
                    reloadFlag=false
                } else {
                    toolService.insertToolByPage(toolPage)
                }

                //preprocess - parse
                //val size = toolPage.size  ** Page 객체가 JSON으로 불러오는 데이터 포맷과 정확히 호환되지 않고 있습니다
                val index = toolPage.pageable.page
                val total = toolPage.total

                //preprocess - logic
                val pageSize = Constants.TOOL_PAGE_SIZE.coerceAtMost(total)
                Log.d("tool","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1

                //event
                handler.post{
                    listener?.onRequestProcessed(TOOL_ALL.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - tool load finished
                    Log.d("tool", "tool insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded()
                    }
                } else{

                    //send 2 - membership load not finished
                    val type = TOOL_ALL
                    val data = "{\"size\":${pageSize},\"page\":${loadingPageIndex}}"
                    send(type, data)

                }
            }

            TOOL_ALL_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //database update
                toolService.resetTable()

                //event
                handler.post{
                    listener?.onRequestProcessed(TOOL_ALL_COUNT.processMessage,1,1)
                }

                //postprocess - logic
                val size = Constants.TOOL_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val type = TOOL_ALL
                val data = "{\"size\":${size},\"page\":${loadingPageIndex}}"
                send(type,data)
            }

            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.name -> {
                //response
                val rentalRequestSheetPage = gson.fromJson(jsonStr, Page::class.java)

                //service update
                rentalRequestSheetService.add(rentalRequestSheetPage)

                //preprocess - parse
                //val size = rentalRequestSheetPage.size  ** Page 객체가 JSON으로 불러오는 데이터 포맷과 정확히 호환되지 않고 있습니다
                val index = rentalRequestSheetPage.pageable.page
                val total = rentalRequestSheetPage.total

                //preprocess - logic
                val pageSize = Constants.SHEET_PAGE_SIZE.coerceAtMost(total)
                Log.d("rentalRequestSheet","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1


                //event
                handler.post{
                    listener?.onRequestProcessed(RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - sheet load finished
                    Log.d("rentalRequestSheet", "rentalRequestSheet insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded()
                    }

                } else{
                    //send 2 - sheet load not finished
                    val toolboxService = ToolboxService.getInstance()
                    val toolbox = toolboxService.getToolbox()

                    val type = RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX
                    val data = "{\"size\":${pageSize},\"page\":${loadingPageIndex},\"toolboxId\":${toolbox.id}}"
                    send(type, data)
                }
            }
            RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //service update
                rentalRequestSheetService.clear()

                //event
                handler.post{
                    listener?.onRequestProcessed(RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT.processMessage,1,1)
                }

                //postprocess - logic
                val size = Constants.SHEET_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val toolboxService = ToolboxService.getInstance()
                val toolbox = toolboxService.getToolbox()

                val type = RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX
                val data = "{\"size\":${size},\"page\":${loadingPageIndex},\"toolboxId\":${toolbox.id}}"
                send(type,data)
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

                val listType = object : TypeToken<List<ToolboxDto>>() {}.type
                val toolboxList: List<ToolboxDto> = gson.fromJson(jsonStr, listType)

                handler.post{
                    DialogUtils.showSingleChoiceDialog("정비실을 선택해주세요.",toolboxList.map{toolboxDto->toolboxDto.name}.toTypedArray()){
                        ToolboxService.getInstance().updateToolbox(toolboxList[it])
                    }
                }
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

    fun setDevice(device: BluetoothDevice) {
        bluetoothDevice=device
        bluetoothCommunicationHandler.setDevice(device)
    }
    fun resetReconnectAttempt(){
        bluetoothCommunicationHandler.resetReconnectAttempt()
    }
    fun startTimer(){
        bluetoothCommunicationHandler.startTimer()
    }

    fun stopTimer(){
        bluetoothCommunicationHandler.stopTimer()
    }

    fun continueRequest() {
        if (lastSendedMessageType != NULL) {
            reloadFlag = true
            send(lastSendedMessageType, lastSendedMessageData)
        }
    }
}