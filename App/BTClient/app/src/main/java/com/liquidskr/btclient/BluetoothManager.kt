package com.liquidskr.btclient

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetDto
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetDto
import com.mrsmart.standard.tag.TagAndToolboxToolLabelDto
import com.mrsmart.standard.tag.TagDto
import com.mrsmart.standard.tag.ToolboxToolLabelDto
import com.liquidskr.btclient.Constants.BluetoothMessageType.*
import com.mrsmart.standard.sheet.outstanding.OutstandingRentalSheetService
import com.mrsmart.standard.sheet.rentalrequest.RentalRequestSheetService
import com.mrsmart.standard.tag.TagService
import com.mrsmart.standard.tag.ToolboxToolLabelService
import com.mrsmart.standard.tool.ToolService
import com.mrsmart.standard.toolbox.ToolboxDto
import com.mrsmart.standard.toolbox.ToolboxService
import java.lang.reflect.Type

class BluetoothManager (private val handler : Handler){

    private val gson = Gson()

    var sendingFlag = false

    interface Listener {
        fun onDisconnected()
        fun onRequestStarted()
        fun onRequestProcessed(context : String, processedAmount : Int , totalAmount : Int)
        fun onRequestEnded(message: String)
        fun onRequestFailed(message : String)
        fun onException(message : String)
    }
    var listener : Listener? = null


    private lateinit var bluetoothDevice : BluetoothDevice
    private val bluetoothCommunicationHandlerListener = object:BluetoothCommunicationHandler.Listener{
        override fun onConnected() {
            Log.i("bluetooth","Connected")
            handler.post{
                listener?.onRequestEnded("블루투스 연결 성공")
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
            Log.d("bluetooth","send complete")
        }

        override fun onException(type: Constants.ExceptionType, description: String) {
            Log.e("bluetooth","exception final : [${type.name}] : [${description}]")
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
    private val outstandingRentalSheetService = OutstandingRentalSheetService.getInstance()
    private val toolboxService = ToolboxService.getInstance()
    private val tagService = TagService.getInstance()
    private val toolboxToolLabelService = ToolboxToolLabelService.getInstance()

    /**
     * -1 : not loading , 0~ : loading index
     * 연결이 끊겼을 때, 혹은 데이터가 손상되었을 때, 다시 요청할 페이지 인덱스.
     * 단일 요청에 대해서는 0으로 설정할 것.
     */
    private var loadingPageIndex : Int = -1
    private var reloadFlag : Boolean = false // false : 안끊김 (insert) , true : 끊겼었음. 재송신중 (upsert)
    private var lastSendedMessageType : Constants.BluetoothMessageType = NULL
    private var lastSendedMessageData : String = ""


    fun send(type:Constants.BluetoothMessageType,data:String){
        if (sendingFlag) {
            Log.e("bluetooth","already sending")
            handler.postDelayed({
                send(type,data)
            },Constants.BLUETOOTH_RESEND_INTERVAL)
            return
        }
        sendingFlag = true
        handler.post {
            Log.d("bluetooth","listener : $listener")
            listener?.onRequestStarted()
        }

        //cache
        lastSendedMessageType = type
        lastSendedMessageData = data

        bluetoothCommunicationHandler.send("$type,$data")
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
                Log.i("membership","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1


                //event
                handler.post{
                    listener?.onRequestProcessed(MEMBERSHIP_ALL.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - membership load finished
                    Log.i("membership", "membership insert complete (size : ${total})")
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

                handler.postDelayed({
                    send(type,data)
                },Constants.BLUETOOTH_FIRST_OF_SUCCESIVE_SEND_DELAY)
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
                Log.i("tool","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1

                //event
                handler.post{
                    listener?.onRequestProcessed(TOOL_ALL.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - tool load finished
                    Log.i("tool", "tool insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded(TOOL_ALL.processEndMessage)
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
                handler.postDelayed({
                    send(type,data)
                },Constants.BLUETOOTH_FIRST_OF_SUCCESIVE_SEND_DELAY)
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
                Log.i("rentalRequestSheet","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1


                //event
                handler.post{
                    listener?.onRequestProcessed(RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - sheet load finished
                    Log.i("outstandingRentalSheet", "outstandingRentalSheet insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded(RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.processEndMessage)
                    }

                } else{
                    //send 2 - sheet load not finished
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
                if (total<1) {
                    handler.post{
                        listener?.onRequestEnded(RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT.processEndMessage)
                    }
                    return
                }

                //postprocess - logic
                val size = Constants.SHEET_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val toolbox = toolboxService.getToolbox()

                val type = RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX
                val data = "{\"size\":${size},\"page\":${loadingPageIndex},\"toolboxId\":${toolbox.id}}"
                handler.postDelayed({
                    send(type,data)
                },Constants.BLUETOOTH_FIRST_OF_SUCCESIVE_SEND_DELAY)
            }

            RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }

            RETURN_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }


            OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX.name -> {
                //response
                val outstandingRentalSheetPage = gson.fromJson(jsonStr, Page::class.java)

                //service update
                outstandingRentalSheetService.add(outstandingRentalSheetPage)

                //preprocess - parse
                //val size = outstandingRentalSheetPage.size  ** Page 객체가 JSON으로 불러오는 데이터 포맷과 정확히 호환되지 않고 있습니다
                val index = outstandingRentalSheetPage.pageable.page
                val total = outstandingRentalSheetPage.total

                //preprocess - logic
                val pageSize = Constants.SHEET_PAGE_SIZE.coerceAtMost(total)
                Log.i("outstandingRentalSheet","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1

                //event
                handler.post{
                    listener?.onRequestProcessed(OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - sheet load finished
                    Log.i("outstandingRentalSheet", "outstandingRentalSheet insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded(OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX.processEndMessage)
                    }

                } else{
                    //send 2 - sheet load not finished
                    val toolbox = toolboxService.getToolbox()

                    val type = OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX
                    val data = "{\"size\":${pageSize},\"page\":${loadingPageIndex},\"toolboxId\":${toolbox.id}}"
                    send(type, data)
                }
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //service update
                outstandingRentalSheetService.clear()

                //event
                handler.post{
                    listener?.onRequestProcessed(OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT.processMessage,1,1)
                }
                if (total<1) {
                    handler.post{
                        listener?.onRequestEnded(OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT.processEndMessage)
                    }
                    return
                }

                //postprocess - logic
                val size = Constants.SHEET_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val toolbox = toolboxService.getToolbox()

                val type = OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX
                val data = "{\"size\":${size},\"page\":${loadingPageIndex},\"toolboxId\":${toolbox.id}}"
                handler.postDelayed({
                    send(type,data)
                },Constants.BLUETOOTH_FIRST_OF_SUCCESIVE_SEND_DELAY)
            }
            OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX.name -> {
                val listType: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //service update
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP.name -> {
                val type: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
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
            RENTAL_REQUEST_SHEET_APPROVE.name -> {
                //response
                val message = gson.fromJson(jsonStr,String::class.java)

                //event
                if (message=="good"){
                    handler.post{
                        listener?.onRequestEnded("") //일관성 대단함
                        rentalRequestSheetService.deleteItem()
                        DialogUtils.showAlertDialog("성공",RENTAL_REQUEST_SHEET_APPROVE.processEndMessage){
                                _,_-> DialogUtils.activity.supportFragmentManager.popBackStack()
                        } //이게되네요... 이게되네...ㅋㅋㅋㅋ
                    }
                }else{
                    handler.post{
                        listener?.onRequestFailed("알수없는오류발생 : $message : RENTAL_REQUEST_SHEET_APPROVE")
                    }
                }
            }
            RETURN_SHEET_FORM.name -> {
                //response
                val message = gson.fromJson(jsonStr,String::class.java)

                //event
                if (message=="good"){
                    handler.post{
                        listener?.onRequestEnded("") //일관성 대단함
                        outstandingRentalSheetService.deleteItem()
                        DialogUtils.showAlertDialog("성공",RETURN_SHEET_FORM.processEndMessage){
                            _,_-> DialogUtils.activity.supportFragmentManager.popBackStack()
                        } //이게되네요... 이게되네...ㅋㅋㅋㅋ
                    }
                }else{
                    handler.post {
                        listener?.onRequestFailed("알수없는오류발생 : $message : RETURN_SHEET_FORM")
                    }
                }
            }
            RETURN_SHEET_REQUEST.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TOOLBOX_TOOL_LABEL_FORM.name -> {
                val pageType: Type = object : TypeToken<String>() {}.type
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
                //response
                val labelPage = gson.fromJson(jsonStr, Page::class.java)

                //database update
                if (reloadFlag){
                    toolboxToolLabelService.upsertToolboxToolLabelByPage(labelPage)
                    reloadFlag=false
                } else {
                    toolboxToolLabelService.insertToolboxToolLabelByPage(labelPage)
                }

                //preprocess - parse
                //val size = toolboxToolLabel.size  ** Page 객체가 JSON으로 불러오는 데이터 포맷과 정확히 호환되지 않고 있습니다
                val index = labelPage.pageable.page
                val total = labelPage.total

                //preprocess - logic
                val pageSize = Constants.TOOLBOX_TOOL_LABEL_PAGE_SIZE.coerceAtMost(total)
                Log.i("toolboxToolLabel","$index / ${total/pageSize} pages inserted. (size : ${pageSize})")
                loadingPageIndex+=1

                //event
                handler.post{
                    listener?.onRequestProcessed(TOOLBOX_TOOL_LABEL_ALL.processMessage,index,total/pageSize)
                }

                //postprocess - logic
                if (loadingPageIndex>total/pageSize) {

                    //send 1 - label load finished
                    Log.i("toolboxToolLabel", "toolboxToolLabel insert complete (size : ${total})")
                    handler.post{
                        listener?.onRequestEnded(TOOLBOX_TOOL_LABEL_ALL.processEndMessage)
                    }

                } else{
                    //send 2 - label load not finished
                    val type = TOOLBOX_TOOL_LABEL_ALL
                    val data = "{\"toolboxId\":${toolboxService.getToolbox().id},\"size\":${pageSize},\"page\":${loadingPageIndex}}"
                    send(type, data)
                }
            }
            TOOLBOX_TOOL_LABEL_ALL_COUNT.name -> {
                //response
                val total = gson.fromJson(jsonStr,Int::class.java)

                //database update
                toolboxToolLabelService.resetTable()

                //event
                handler.post{
                    listener?.onRequestProcessed(TOOLBOX_TOOL_LABEL_ALL_COUNT.processMessage,1,1)
                }

                //postprocess - logic
                val size = Constants.TOOLBOX_TOOL_LABEL_PAGE_SIZE.coerceAtMost(total)
                loadingPageIndex=0

                //send
                val type = TOOLBOX_TOOL_LABEL_ALL
                val data = "{\"toolboxId\":${toolboxService.getToolbox().id},\"size\":${size},\"page\":${loadingPageIndex}}"
                handler.postDelayed({
                    send(type,data)
                },Constants.BLUETOOTH_FIRST_OF_SUCCESIVE_SEND_DELAY)
            }
            TAG_GROUP.name -> {
                val type: Type = object : TypeToken<TagDto>() {}.type
                TODO("not implemented yet")
            }
            OUTSTANDING_RENTAL_SHEET_BY_TAG.name -> {
                //response
                val outstandingRentalSheet = gson.fromJson(jsonStr, OutstandingRentalSheetDto::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(outstandingRentalSheet)
                }

                loadingPageIndex=0
            }
            OUTSTANDING_RENTAL_SHEET_ID_BY_TAG.name->{
                //response
                val id = gson.fromJson(jsonStr,Long::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(id)
                }

                loadingPageIndex=0
            }
            TAG.name -> {
                //response
                val tagDto = gson.fromJson(jsonStr, TagDto::class.java)

                //service update
                handler.post {
                    tagService.handleResponse(tagDto)
                }

                loadingPageIndex=0
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
                //response
                val message = gson.fromJson(jsonStr,String::class.java)

                if (message=="good"){
                    handler.post{
                        listener?.onRequestEnded("") //TODO:일관성
                        rentalRequestSheetService.deleteItem()
                        DialogUtils.showAlertDialog("성공",RENTAL_REQUEST_SHEET_CANCEL.processEndMessage){
                                _,_-> DialogUtils.activity.supportFragmentManager.popBackStack()
                        } //TODO:이거 어떻게 쌈빡한 방법이 없나?
                    }
                }else{
                    handler.post{
                        listener?.onRequestFailed("알수없는오류발생 : $message : RENTAL_REQUEST_SHEET_APPROVE")
                    }
                }
            }
            RENTAL_REQUEST_SHEET_APPLY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                TODO("not implemented yet")
            }
            TAG_AND_TOOLBOX_TOOL_LABEL_FORM.name -> {
                //response
                val resultLabel = gson.fromJson(jsonStr,ToolboxToolLabelDto::class.java)

                //event
                handler.post{
                    listener?.onRequestEnded("") //일관성 대단함
                    DialogUtils.showAlertDialog("성공",TAG_AND_TOOLBOX_TOOL_LABEL_FORM.processEndMessage){
                        _,_->
                        toolboxToolLabelService.update(resultLabel)

                        DialogUtils.activity.supportFragmentManager.popBackStack()
                    } //이게되네요... 이게되네...ㅋㅋㅋㅋ
                }
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
                    listener?.onRequestProcessed(TOOLBOX_ALL.processMessage,1,1)
                    DialogUtils.showSingleChoiceDialog("정비실을 선택해주세요.",toolboxList.map{toolboxDto->toolboxDto.name}.toTypedArray()){
                        toolboxService.updateToolbox(toolboxList[it])
                        listener?.onRequestEnded(TOOLBOX_ALL.processEndMessage)
                    }
                }
            }
            TAG_LIST_BY_TOOL_AND_TOOLBOX_ID.name -> {
                //response
                val tagList = gson.fromJson(jsonStr, List::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(tagList)
                }

                loadingPageIndex=0
            }
            TAG_LIST_BY_TOOLBOX_TOOL_LABEL_QRCODE.name -> {
                //response
                val tagList = gson.fromJson(jsonStr, List::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(tagList)
                }

                loadingPageIndex=0
            }
            TAG_LIST_BY_TAG_MACADDRESS.name -> {
                //response
                val tagList = gson.fromJson(jsonStr, List::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(tagList)
                }

                loadingPageIndex=0
            }
            TOOLBOX_TOOL_LABEL_AVAILABLE.name -> {
                //response
                val toolboxToolLabel = gson.fromJson(jsonStr, String::class.java)

                //service update
                handler.post{
                    toolboxToolLabelService.handleResponse(toolboxToolLabel)
                }

                loadingPageIndex=0
            }
            TAG_AVAILABLE.name -> {
                //response
                val tag = gson.fromJson(jsonStr, String::class.java)

                //service update
                handler.post{
                    tagService.handleResponse(tag)
                }

                loadingPageIndex=0
            }

            // ###################

            DATA_TYPE_EXCEPTION.name->{
                handler.post{
                    listener?.onException(jsonStr);
                }
            }
            DATA_SEMANTIC_EXCEPTION.name->{
                handler.post{
                    listener?.onException(jsonStr);
                }
            }
            UNKNOWN_EXCEPTION.name->{
                handler.post{
                    listener?.onException(jsonStr);
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

            else -> {
                Log.e("bluetooth","unknown type : $typeStr")
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

    fun endRequest(message:String) {
        listener?.onRequestEnded(message)
    }

    companion object {
        private var instance: BluetoothManager? = null

        /**
         * only activity can call this method
         */
        fun getInstance(handler: Handler): BluetoothManager {
            if (instance == null) {
                instance = BluetoothManager(handler)
            }
            return instance!!
        }

        /**
         * for non-activity class
         */
        fun getInstance(): BluetoothManager {
            if (instance == null) {
                throw Exception("BluetoothManager instance is not initialized")
            }
            return instance!!
        }
    }
}