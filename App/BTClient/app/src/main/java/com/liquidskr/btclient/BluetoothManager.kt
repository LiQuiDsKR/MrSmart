package com.liquidskr.btclient

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolboxToolLabelDto
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.util.LinkedList
import java.util.Queue
import java.util.UUID

class BluetoothManager (private val context: Context, private val activity: Activity) {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter

    var isSending: Boolean = false
    private val messageQueue: Queue<BluetoothMessage> = LinkedList()


    private var timeoutHandler: Handler = Handler(Looper.getMainLooper())

    var gson = Gson()
    var timeout = false
    fun bluetoothOpen() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val permissionManager = PermissionManager(activity)
            permissionManager.checkAndRequestPermission()
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size > 0) {
                for (device in pairedDevices) {
                    if (device.name == "DESKTOP-0E0EKMO" || device.name=="LQD") { // 연결하려는 디바이스의 이름을 지정하세요.
                        bluetoothDevice = device
                        break
                    }
                }
            } else {
                Toast.makeText(context, "연결된 기기가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP (Serial Port Profile) UUID
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket.connect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            standbyProcess()
        } catch (e:Exception) {
            Toast.makeText(activity, "블루투스 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    fun bluetoothClose() {
        bluetoothSocket.close()
    }
    interface RequestCallback {
        fun onSuccess(result: String, type: Type)
        fun onError(e: Exception)
    }
    private fun performSend(type: RequestType, params: String, callback: RequestCallback) {
        if (!bluetoothSocket.isConnected) {
            callback.onError(IOException("Bluetooth socket is not connected"))
            return
        }

        isSending = true
        try {
            // 앱에서 서버로 type 전송.
            outputStream = bluetoothSocket.outputStream
            var sendMsg: ByteArray = byteArrayOf()
            sendMsg += type.name.toByteArray(Charsets.UTF_8)
            sendMsg += ",".toByteArray(Charsets.UTF_8)
            sendMsg += params.toByteArray(Charsets.UTF_8)

            outputStream.write(sendMsg)
            outputStream.flush()
            Log.d("SEND", type.name)
        } catch (e: Exception) {
            // 전송 중 에러
            callback.onError(e)
        }

        val timeoutRunnable = Runnable {
            if (!timeout) {
                timeout = true
                dataSend("TIMEOUT")
            }

            // Timeout 시, 메시지 전송 완료로 처리
            isSending = false
            if (messageQueue.isNotEmpty()) {
                val nextMessage = messageQueue.poll()
                performSend(nextMessage.type, nextMessage.params, nextMessage.callback)
            }
        }

        // receive loop를 돌리는 thread 선언
        val thread = Thread {
            timeoutHandler.postDelayed(
                timeoutRunnable,
                10000
            )
            // 나머지 코드는 그대로 유지
            try {
                inputStream = bluetoothSocket.inputStream

                val lengthBuffer = ByteArray(4) // 길이는 int로 받겠습니다
                inputStream.read(lengthBuffer,0,4)
                val dataSize = ByteBuffer.wrap(lengthBuffer).int

                val dataBuffer = ByteArray(1024) //한 번에 받을 byteArray단위
                val byteStream = ByteArrayOutputStream() //최종 byteStream
                var bytesRead: Int = 0

                while (bytesRead < dataSize) {
                    val result = inputStream.read(dataBuffer, 0, 1024)
                    if (result <= 0) {
                        break
                    }
                    byteStream.write(dataBuffer, 0, result) // 이 부분 추가
                    bytesRead += result
                }
                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거

                val byteArray = byteStream. toByteArray()

                //정상적으로 데이터를 받았다면
                if (byteArray.isNotEmpty()) {
                    Log.d("Received", String(byteArray, Charsets.UTF_8))
                    clearSendingState() // 무언가 받았으므로, 송수신 정상이며 isSending 플래그 처리

                    val jsonString = String(byteArray, Charsets.UTF_8)
                    //RequestType별로 인스턴스 생성
                    when (type) {
                        RequestType.MEMBERSHIP_ALL -> {
                            val listType: Type = object : TypeToken<List<Membership>>() {}.type
                            callback.onSuccess(jsonString,listType)
                        }

                        RequestType.TOOL_ALL -> {
                            val listType: Type = object : TypeToken<List<ToolDto>>() {}.type
                            callback.onSuccess(jsonString, listType)
                        }

                        RequestType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX ->{
                            val pageType: Type = object : TypeToken<Page>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }

                        RequestType.RENTAL_SHEET_PAGE_BY_MEMBERSHIP ->{
                            val pageType: Type = object : TypeToken<Page>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }

                        RequestType.RETURN_SHEET_PAGE_BY_MEMBERSHIP ->{
                            val pageType: Type = object : TypeToken<Page>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }

                        RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP ->{
                            val pageType: Type = object : TypeToken<Page>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }

                        RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX ->{
                            val pageType: Type = object : TypeToken<Page>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }
                        RequestType.RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX ->{
                            val listType: Type = object : TypeToken<List<RentalRequestSheetDto>>() {}.type
                            callback.onSuccess(jsonString, listType)
                        }
                        RequestType.RENTAL_REQUEST_SHEET_FORM ->{
                            val type: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP ->{
                            val type: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.RENTAL_REQUEST_SHEET_APPROVE ->{
                            val type: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.RETURN_SHEET_FORM ->{
                            val type: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.RENTAL_REQUEST_SHEET_APPROVE ->{
                            val pageType: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }
                        RequestType.TOOLBOX_TOOL_LABEL_FORM ->{
                            val pageType: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, pageType)
                        }
                        RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX ->{
                            val listType: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                            callback.onSuccess(jsonString, listType)
                        }
                        RequestType.RETURN_SHEET_REQUEST ->{
                            val type: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TAG_FORM ->{
                            val type: Type = object : TypeToken<String>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TOOLBOX_TOOL_LABEL ->{
                            val type: Type = object : TypeToken<ToolboxToolLabelDto>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TAG_LIST ->{
                            val type: Type = object : TypeToken<List<String>>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TAG_ALL ->{
                            val type: Type = object : TypeToken<List<TagDto>>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TOOLBOX_TOOL_LABEL_ALL ->{
                            val type: Type = object : TypeToken<List<ToolboxToolLabelDto>>() {}.type
                            callback.onSuccess(jsonString, type)
                        }
                        RequestType.TAG_GROUP ->{
                        val type: Type = object : TypeToken<TagDto>() {}.type
                        callback.onSuccess(jsonString, type)
                    }
                    }
                }
            } catch (e: Exception) {
                //수신 중 에러
                callback.onError(e)
            }
        }
        thread.start()
    }
    fun requestData(type:RequestType,params:String,callback:RequestCallback){
        if (!isSending) {
            performSend(type, params, callback)
        } else {
            // 메시지 전송 중일 때는 큐에 추가
            messageQueue.offer(BluetoothMessage(type, params, callback))
        }
    }
    private fun clearSendingState() {
        isSending = false
        if (messageQueue.isNotEmpty()) {
            val nextMessage = messageQueue.poll()
            performSend(nextMessage.type, nextMessage.params, nextMessage.callback)
        }
    }

    fun dataSend(sendingData: String) {
        try {
            outputStream = bluetoothSocket.outputStream
            outputStream.write(sendingData.toByteArray())
            outputStream.flush()
            Log.d("SEND", sendingData)
        } catch (e: Exception) {
            Log.d("mDataOuputStream Error", e.toString())
        }
    }

    fun standbyRentalProcess(json: String) {
        requestData(RequestType.RENTAL_REQUEST_SHEET_APPROVE, json, object:
            BluetoothManager.RequestCallback{
            override fun onSuccess(result: String, type: Type) {
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun standbyProcess() {
        // 보류 항목 모두 전송
        var dbHelper = DatabaseHelper(context)
        val rentalList = dbHelper.getRentalStandby()
        val returnList = dbHelper.getReturnStandby()
        for ((id, json) in rentalList) {
            try {
                requestData(RequestType.RENTAL_REQUEST_SHEET_APPROVE, json, object:
                    BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        dbHelper.updateStandbyStatus(id)
                    }
                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }
                })
            } catch (e: IOException) {
                Log.d("standby","cannot send rental standby")
            }
        }
        for ((id, json) in returnList) {
            try {
                requestData(RequestType.RETURN_SHEET_FORM, json, object:
                    BluetoothManager.RequestCallback{
                    override fun onSuccess(result: String, type: Type) {
                        dbHelper.updateStandbyStatus(id)
                    }

                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }
                })
            } catch (e: IOException) {
                Log.d("standby","cannot send return standby")
            }
        }
        dbHelper.close()
    }
}
