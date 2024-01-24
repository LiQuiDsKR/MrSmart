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
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalRequestSheetDto
import com.mrsmart.standard.tool.TagAndToolboxToolLabelDto
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolboxCompressDto
import com.mrsmart.standard.tool.ToolboxDto
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
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket

    private lateinit var receiveThread: Thread
    private val handler = Handler(Looper.getMainLooper())

    var isConnected = false

    //var isSending: Boolean = false
    //private val messageQueue: Queue<BluetoothMessage> = LinkedList()

    var pcName: String = "정비실PC이름"

    private var bluetoothDataListener: BluetoothDataListener? = null
    private var bluetoothConnectionListener: BluetoothConnectionListener? = null

    fun setBluetoothDataListener(listener: BluetoothDataListener) {
        this.bluetoothDataListener = listener
    }

    interface RequestCallback {
        fun onSuccess(result: String, type: Type)
        fun onError(e: Exception)
    }

    interface BluetoothDataListener {
        fun onSuccess(result: String, type: Type)
        fun onError(e: Exception)
    }

    interface BluetoothConnectionListener {
        fun onBluetoothDisconnected()
        fun onBluetoothConnected()
    }

    fun setBluetoothConnectionListener (listener: BluetoothConnectionListener) {
        this.bluetoothConnectionListener = listener
    }


    var gson = Gson()
    fun bluetoothOpen() {
        try {
            try {
                bluetoothClose()
            } catch (e: Exception) {
                Log.d("bluetoothError", e.toString())
            }
            val dbHelper = DatabaseHelper(context)
            pcName = dbHelper.getDeviceName()
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val permissionManager = PermissionManager(activity)
            permissionManager.checkAndRequestPermission()
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size > 0) {
                var flag = false// 서버와 연결 부분
                for (device in pairedDevices) {
                    if (device.name == pcName) {
                        bluetoothDevice = device
                        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP (Serial Port Profile) UUID
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
                        try {
                            bluetoothSocket.connect()
                            Toast.makeText(context, "연결에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            isConnected = true
                            flag = true
                        } catch (e: IOException) {
                            isConnected = false
                            Log.d("bluetooth", e.toString())
                            // Toast.makeText(context, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        break
                    }
                }
                if (!flag) {
                    isConnected = false
                    Toast.makeText(context, "[${pcName}] 에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                isConnected = false
                Toast.makeText(context, "페어링된 기기가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            receiveThread = Thread {
                try {
                    inputStream = bluetoothSocket.inputStream
                    var dataSize = -1
                    val outputStream = ByteArrayOutputStream() // 최종 byteStream
                    while (bluetoothSocket.isConnected) {
                        val size = inputStream.available()
                        if (size > 0) {
                            val readDatas = ByteArray(size)
                            inputStream.read(readDatas, 0, size)
                            outputStream.write(readDatas)
                            if (dataSize == -1 && outputStream.size() > 4) {
                                try {
                                    dataSize = ByteBuffer.wrap(outputStream.toByteArray(), 0, 4).int
                                } catch (e:Exception) {
                                    Log.d("datasize_toInt","cannot convert to Int")
                                }
                                // dataSize = ByteBuffer.wrap(outputStream.toByteArray(), 0, 4).int
                                val remainingBytes = outputStream.toByteArray().drop(4).toByteArray()
                                if (remainingBytes.isNotEmpty()) {
                                    Log.d("readData(HEX)",byteArrayToHex(remainingBytes))
                                    Log.d("readData(STR)", String(remainingBytes, Charsets.UTF_8))
                                    outputStream.reset()
                                    if (remainingBytes.isNotEmpty()) {
                                        outputStream.write(remainingBytes)
                                        outputStream.flush()
                                    }
                                }
                            }
                            if (dataSize > -1 && outputStream.size() >= (dataSize)) {
                                var datas = ByteBuffer.wrap(outputStream.toByteArray(), 0, dataSize)
                                Log.d("writeData(HEX)",byteArrayToHex(datas.array()))
                                Log.d("writeData(STR)", String(datas.array(), Charsets.UTF_8))
                                val remainingBytes = outputStream.toByteArray().drop(dataSize).toByteArray()
                                outputStream.reset()
                                if (remainingBytes.isNotEmpty()) {
                                    outputStream.write(remainingBytes)
                                    outputStream.flush()
                                }
                                //clearSendingState()
                                processData(datas.array())
                                dataSize = -1
                            }
                        } else if (size == 0) {
                            if (bluetoothSocket.isConnected) {
                                continue
                            } else {
                                isConnected = false
                                Log.d("bluetooth_","Disconnected1")
                                Toast.makeText(context, "블루투스 연결이 끊겼습니다. 다시 연결해주세요.",Toast.LENGTH_SHORT).show()
                                handler.post {
                                    bluetoothConnectionListener?.onBluetoothDisconnected()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Exception", e.toString())
                }
            }
            receiveThread.start()
        } catch (e:Exception) {
            Toast.makeText(activity, "블루투스 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun bluetoothClose() {
        if (bluetoothSocket != null) bluetoothSocket.close()
    }

    fun requestData(type:RequestType, params:String, callback:RequestCallback) {
        performSend(type, params, callback)
        /*
        if (!isSending) {
            performSend(type, params, callback)
        } else {
            // 메시지 전송 중일 때는 큐에 추가
            messageQueue.offer(BluetoothMessage(type, params, callback))
        }*/
    }

    private fun performSend(type: RequestType, params: String, callback: RequestCallback) {
        try {
            outputStream = bluetoothSocket.outputStream
            var sendMsg: ByteArray = byteArrayOf()
            sendMsg += type.name.toByteArray(Charsets.UTF_8)
            sendMsg += ",".toByteArray(Charsets.UTF_8)
            sendMsg += params.toByteArray(Charsets.UTF_8)

            val dataSize = sendMsg.size

            val byteBuffer = ByteBuffer.allocate(4)
            byteBuffer.putInt(dataSize)
            val sizeByte = byteBuffer.array()

            val finalMessage = sizeByte + sendMsg

            var buffer = ByteBuffer.allocate(1024)
            var chunkSize = 0
            var offset = 0
            while (offset < finalMessage.size) {
                chunkSize = minOf(buffer.remaining(), finalMessage.size - offset)
                buffer.put(finalMessage, offset, chunkSize)

                offset += chunkSize

                if (!buffer.hasRemaining()) {

                    buffer.flip()
                    val byteArray = ByteArray(buffer.remaining())
                    buffer.get(byteArray)
                    outputStream.write(byteArray)
                    outputStream.flush()
                    Log.d("bluetooth_SendData", byteArrayToHex(buffer.array()))
                    buffer.clear()
                }
            }
            if (buffer.position() > 0) {
                buffer.flip()
                val byteArray = ByteArray(buffer.remaining())
                buffer.get(byteArray)
                outputStream.write(byteArray)
                outputStream.flush()
                Log.d("bluetooth_SendData_Last", byteArrayToHex(buffer.array()))
            }

            // 전송 완료

            setBluetoothDataListener(object : BluetoothDataListener {
                override fun onSuccess(result: String, type: Type) {
                    callback.onSuccess(result, type)
                }
                override fun onError(e: Exception) {
                }
            })
        } catch (e: Exception) {
            // 전송 중 에러
            bluetoothConnectionListener?.onBluetoothDisconnected()
            handler.post {
                Log.d("bluetooth_","Disconnected1")
            }
            callback.onError(e)
        }
    }
    fun byteArrayToHex(byteArray: ByteArray): String {
        val hexChars = CharArray(byteArray.size * 2)
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v shr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun processData(data: ByteArray) {
        val receivedString = String(data, Charsets.UTF_8)
        val (type, jsonString) = parseInputString(receivedString)

        when (type) {
            RequestType.MEMBERSHIP_ALL.name -> {
                val listType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }

            RequestType.MEMBERSHIP_ALL_COUNT.name -> {
                val listType: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }

            RequestType.TOOL_ALL.name -> {
                val listType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }

            RequestType.TOOL_ALL_COUNT.name -> {
                val listType: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }

            RequestType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }

            RequestType.RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }

            RequestType.RETURN_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }

            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }

            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX.name -> {
                val pageType: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }
            RequestType.RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX.name -> {
                val listType: Type = object : TypeToken<List<RentalRequestSheetDto>>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }
            RequestType.RENTAL_REQUEST_SHEET_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP.name -> {
                val type: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_APPROVE.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RETURN_SHEET_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_APPROVE.name -> {
                val pageType: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }
            RequestType.TOOLBOX_TOOL_LABEL_FORM.name -> {
                val pageType: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, pageType)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX.name -> {
                val listType: Type = object : TypeToken<List<OutstandingRentalSheetDto>>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, listType)
            }
            RequestType.RETURN_SHEET_REQUEST.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TOOLBOX_TOOL_LABEL.name -> {
                val type: Type = object : TypeToken<ToolboxToolLabelDto>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_LIST.name -> {
                val type: Type = object : TypeToken<List<String>>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TOOLBOX_TOOL_LABEL_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TOOLBOX_TOOL_LABEL_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_GROUP.name -> {
                val type: Type = object : TypeToken<TagDto>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_BY_TAG.name -> {
                val type: Type = object : TypeToken<OutstandingRentalSheetDto>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG.name -> {
                val type: Type = object : TypeToken<TagDto>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_FORM_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_APPROVE_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RETURN_SHEET_FORM_STANDBY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_CANCEL.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.RENTAL_REQUEST_SHEET_APPLY.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_AND_TOOLBOX_TOOL_LABEL_FORM.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TAG_AND_TOOLBOX_TOOL_LABEL.name -> {
                val type: Type = object : TypeToken<TagAndToolboxToolLabelDto>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_ALL.name -> {
                val type: Type = object : TypeToken<Page>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
            RequestType.TOOLBOX_ALL.name -> {
                val type: Type = object : TypeToken<List<ToolboxCompressDto>>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }

            // ###################
            RequestType.TEST.name -> {
                val type: Type = object : TypeToken<String>() {}.type
                bluetoothDataListener?.onSuccess(jsonString, type)
            }
        }
    }
    /*
    private fun clearSendingState() {
        isSending = false
        if (messageQueue.isNotEmpty()) {
            val nextMessage = messageQueue.poll()
            performSend(nextMessage.type, nextMessage.params, nextMessage.callback)
        }
    }*/

    private fun parseInputString(input: String): Pair<String, String> {
        val index = input.indexOf(',')
        if (index != -1) {
            val beforeComma = input.substring(0, index)
            val afterComma = input.substring(index + 1)
            return Pair(beforeComma, afterComma)
        } else {
            // 적절한 처리가 필요한 경우, 예외 또는 기본값을 반환할 수 있습니다.
            return Pair(input, "")
        }
    }
    fun standbyProcess() {
        var dbHelper = DatabaseHelper(context)
        var isReady = true
        var standbyList = dbHelper.getAllStandbyWithId()
        for (standby in standbyList) {
            when (standby.second.type) {
                "RENTALREQUEST" -> {
                    try {
                        isReady = false
                        requestData(RequestType.RENTAL_REQUEST_SHEET_FORM_STANDBY, standby.second.json, object :
                                BluetoothManager.RequestCallback {
                                override fun onSuccess(result: String, type: Type) {
                                    Log.d("standby",result)
                                    try {
                                        if (result == "good") {
                                            isReady = true
                                            dbHelper.updateStandbyStatus(standby.first)
                                        } else {
                                            Log.d("standby", "updateStandbyStatus not called because result is not 'good'")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("standby", "Error updating standby status", e)
                                    }
                                }

                                override fun onError(e: Exception) {
                                    e.printStackTrace()
                                }
                            })
                    } catch (e: IOException) {
                        Log.d("standby", "cannot send return standby")
                    }
                }
                "RENTAL" -> {
                    isReady = false
                    try {
                        requestData(RequestType.RENTAL_REQUEST_SHEET_APPROVE_STANDBY, standby.second.json, object:
                            BluetoothManager.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                Log.d("standby",result)
                                try {
                                    if (result == "good") {
                                        isReady = true
                                        dbHelper.updateStandbyStatus(standby.first)
                                    } else {
                                        Log.d("standby", "updateStandbyStatus not called because result is not 'good'")
                                    }
                                } catch (e: Exception) {
                                    Log.e("standby", "Error updating standby status", e)
                                }
                            }
                            override fun onError(e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    } catch (e: IOException) {
                        Log.d("standby","cannot send rental standby")
                    }
                }
                "RETURN" -> {
                    isReady = false
                    try {
                        requestData(RequestType.RETURN_SHEET_FORM_STANDBY, standby.second.json, object:
                            BluetoothManager.RequestCallback{
                            override fun onSuccess(result: String, type: Type) {
                                Log.d("standby",result)
                                try {
                                    if (result == "good") {
                                        isReady = true
                                        dbHelper.updateStandbyStatus(standby.first)
                                    } else {
                                        Log.d("standby", "updateStandbyStatus not called because result is not 'good'")
                                    }
                                } catch (e: Exception) {
                                    Log.e("standby", "Error updating standby status", e)
                                }
                            }

                            override fun onError(e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    } catch (e: IOException) {
                        Log.d("standby","cannot send return standby")
                    }
                }
            }
            var delaySecond = 5
            while (!isReady) {
                if (delaySecond == 0) {
                    break
                }
                delaySecond--
                Thread.sleep(1000)
            }
        }
        dbHelper.close()
    }
}
