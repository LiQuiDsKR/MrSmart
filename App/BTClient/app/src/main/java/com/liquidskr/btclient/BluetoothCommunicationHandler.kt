package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import com.liquidskr.btclient.Constants.BLUETOOTH_MAX_RECONNECT_ATTEMPT
import com.liquidskr.btclient.Constants.HEARTBEAT_INTERVAL
import com.liquidskr.btclient.Constants.INITIAL_MESSAGE_DELAY
import com.liquidskr.btclient.Constants.VALIDCHECK_INTERVAL
import com.liquidskr.btclient.Constants.ConnectionState
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

/**
 * 1-1. BluetoothDevice 정보 초기화
 * 1-2. 초기화된 정보를 주입하여 BluetoothConnectionHandler를 생성
 * 1-3. ConnectionHandler의 이벤트 리스너 보유
 *
 * 2-1. BluetoothMessageParser를 생성
 * 2-2. BluetoothMessageParser의 이벤트 리스너 보유
 *
 */
class BluetoothCommunicationHandler (
    private val listener : Listener,
){

    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onReconnectStarted()
        fun onReconnectFailed()
        fun onDataArrived(data: String)
        fun onDataSent(data: String)
        fun onException(type:Constants.ExceptionType, description: String)
    }

    private val bluetoothMessageParserlistener : BluetoothMessageParser.Listener = object : BluetoothMessageParser.Listener {
        override fun onDataArrived(data: ByteArray) {
            listener.onDataArrived(String(data,Charsets.UTF_8))
            commTimeInMillis=0L
        }
        override fun onException(type: Constants.ExceptionType, description: String) {
            bluetoothConnectionHandler.close()
            listener.onException(type,description)
        }
    }
    private val bluetoothMessageParser : BluetoothMessageParser = BluetoothMessageParser(bluetoothMessageParserlistener)

    private val bluetoothConnectionHandlerListener :BluetoothConnectionHandler.Listener = object :BluetoothConnectionHandler.Listener{
        override fun onConnected() {
            connectionState=ConnectionState.CONNECTED
            Log.i("bluetooth", "블루투스 연결에 성공했습니다")
            reconnectAttempt=0
            commTimeInMillis=0
            listener.onConnected()
        }

        override fun onDisconnected() {
            connectionState=ConnectionState.DISCONNECTED
            isBluetoothConnectionHandlerNull=true
            listener.onDisconnected()
        }

        override fun onDataArrived(data: ByteArray) {
            var data = data
            while (data.isNotEmpty()) {
                data = bluetoothMessageParser.process(data)
            }
        }

        override fun onDataSent(datas: ByteArray) {
            Log.d("bluetooth", "send complete : ${byteArrayToHex(datas)}")
            commTimeInMillis=Calendar.getInstance().timeInMillis
        }

        override fun onException(type: Constants.ExceptionType, description: String) {
            listener.onException(type,description)
        }
    }
    private lateinit var bluetoothConnectionHandler : BluetoothConnectionHandler
    // ** var ▲ **
    private var isBluetoothConnectionHandlerNull:Boolean=true
    private var connectionState : ConnectionState = ConnectionState.DISCONNECTED
    private var reconnectAttempt:Int =0
    private var commTimeInMillis : Long = 0L


    private val validCheckTimer = Timer()
    private val heartBeatTimer = Timer()
    private val validCheckTimerTask = object : TimerTask() {
        override fun run() {
            val diff: Long = Calendar.getInstance().timeInMillis - commTimeInMillis
            val timeoutFlag =
                commTimeInMillis != 0L && diff >= Constants.COMMUNICATION_TIMEOUT // true : timeout / false : normal

            Log.v(
                "bluetooth",
                "reAtmp:$reconnectAttempt isNull:$isBluetoothConnectionHandlerNull, connection : $connectionState, cmTime:$commTimeInMillis"
            )
            if(connectionState==ConnectionState.CONNECTING){
                return
            }else if ( connectionState == ConnectionState.DISCONNECTED) {
                reconnect()
            }else if ( timeoutFlag ){
                disconnect()
            }
        }
    }
    private val heartBeatTimerTask = object : TimerTask() {
        override fun run() {
            if (isBluetoothConnectionHandlerNull || commTimeInMillis>0) return
            val now = Calendar.getInstance().timeInMillis
            Log.v("bluetooth", "HeartBeat set : $now")
            send(Constants.BluetoothMessageType.HI.name + ",${Calendar.getInstance().timeInMillis}")
        }
    }

    private lateinit var bluetoothDevice: BluetoothDevice

    init {
        connectionState=ConnectionState.CONNECTING
        isBluetoothConnectionHandlerNull=false
    }


    fun send(message:String){
        Log.d("bluetooth","send message : ${message}")
        val data :ByteArray = message.toByteArray()
        val buffer : ByteBuffer = ByteBuffer.allocate(4)
        buffer.putInt(data.size)
        val sizeByte = buffer.array()

        val outputStream = ByteArrayOutputStream()
        try{
            outputStream.write(sizeByte)
            outputStream.write(data)
        }catch(e:IOException){
            e.printStackTrace()
        }

        val finalData=outputStream.toByteArray()
        bluetoothConnectionHandler.send(finalData)
        listener.onDataSent(message)
    }

    fun connect(){
        connectionState=ConnectionState.CONNECTING
        // -> ConnectionHandler.Listener. onConnected() 시 CONNECTED로 설정됨

        bluetoothConnectionHandler= BluetoothConnectionHandler(bluetoothConnectionHandlerListener,bluetoothDevice)
        isBluetoothConnectionHandlerNull=false
    }

    fun disconnect(){
        Log.d("bluetooth","Comm : disconnecting...")
        bluetoothConnectionHandler.close() // 이 경우, reconnect는 close()로 인한 onDisconnected()에서 실행.
        connectionState=ConnectionState.DISCONNECTED
        isBluetoothConnectionHandlerNull=true
    }
    fun reconnect(){
        connectionState=ConnectionState.CONNECTING
        isBluetoothConnectionHandlerNull=true

        //시도를 충분히 했는데도 재접속 안됨
        if (reconnectAttempt>= Constants.BLUETOOTH_MAX_RECONNECT_ATTEMPT){
            listener.onReconnectFailed()
            return
        }
        //첫 끊김 => fragment 전환해야함
        if (reconnectAttempt<1) {
            listener.onReconnectStarted()
        }
        //그 외
        reconnectAttempt+=1
        val reconnectAttempt = reconnectAttempt
        Log.d("bluetooth","bluetoothReconnecting... Attempt : $reconnectAttempt")

        connect()
    }

    fun resetReconnectAttempt(){
        reconnectAttempt=0
    }

    fun setDevice(device : BluetoothDevice){
        bluetoothDevice=device
        connect()
    }

    fun startTimer(){
        validCheckTimer.schedule(validCheckTimerTask, INITIAL_MESSAGE_DELAY, VALIDCHECK_INTERVAL)
        heartBeatTimer.schedule(heartBeatTimerTask, INITIAL_MESSAGE_DELAY, HEARTBEAT_INTERVAL)
    }

    fun stopTimer(){
        validCheckTimer.cancel()
        heartBeatTimer.cancel()
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

}