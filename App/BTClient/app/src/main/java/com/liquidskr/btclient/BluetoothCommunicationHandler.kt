package com.liquidskr.btclient

import android.util.Log
import com.liquidskr.btclient.Constants.BLUETOOTH_MAX_RECONNECT_ATTEMPT
import com.liquidskr.btclient.Constants.BLUETOOTH_RECONNECT_INTERVAL
import com.liquidskr.btclient.Constants.INITIAL_MESSAGE_DELAY
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

class BluetoothCommunicationHandler (
    private val listener : Listener
){

    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onReconnectTry(reconnectAttempt: Int)
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
            Log.d("bluetooth", "블루투스 연결에 성공했습니다")
            reconnectAttempt=0
            listener.onConnected()
        }

        override fun onDisconnected() {

            validCheckTimer.cancel()
            heartBeatTimer.cancel()
            isBluetoothConnectionHandlerNull=true

            if (reconnectAttempt<1) {
                listener.onDisconnected()
                return
            }
            if (reconnectAttempt> Constants.BLUETOOTH_MAX_RECONNECT_ATTEMPT){
                listener.onReconnectTry(reconnectAttempt)
                return
            }
            reconnectAttempt+=1
            Log.d("bluetooth","bluetoothReconnecting... Attempt : $reconnectAttempt")
            listener.onReconnectTry(reconnectAttempt)
            Thread.sleep(Constants.BLUETOOTH_RECONNECT_INTERVAL)
            connect()
        }

        override fun onDataArrived(data: ByteArray) {
            commTimeInMillis=Calendar.getInstance().timeInMillis
            var data = data
            while (data.isNotEmpty()) {
                data = bluetoothMessageParser.process(data)
            }
        }

        override fun onDataSent(datas: ByteArray) {
            Log.d("bluetooth", "send complete : ${byteArrayToHex(datas)}")
        }

        override fun onException(type: Constants.ExceptionType, description: String) {
            listener.onException(type,description)
        }
    }
    private var bluetoothConnectionHandler : BluetoothConnectionHandler = BluetoothConnectionHandler(bluetoothConnectionHandlerListener)
    // ** var ▲ **
    private var isBluetoothConnectionHandlerNull:Boolean=true
    private var reconnectAttempt:Int =0
    private var commTimeInMillis : Long = 0L
    private var validCheckTimer: Timer = Timer()
    private var heartBeatTimer: Timer = Timer()

    private val validCheckTimerTask = object : TimerTask() {
        override fun run() {
            val isValid = isValid()
            Log.d("bluetooth", "connection valid check : $isValid")
            if (!isValid) {
                disconnect()
            }
        }
    }
    private val heartBeatTimerTask = object : TimerTask() {
        override fun run() {
            val now = Calendar.getInstance().timeInMillis
            Log.d("bluetooth", "HeartBeat set : $now")
            send(Constants.BluetoothMessageType.HI.name + ",${Calendar.getInstance().timeInMillis}")
        }
    }

    fun send(message:String){
        Log.d("bluetooth","send message : ${message}")
        val data :ByteArray = message.toByteArray()
        val buffer : ByteBuffer = ByteBuffer.allocate(4)
        buffer.putInt(data.size)
        val sizeByte = buffer.array()

        val outputStream : ByteArrayOutputStream = ByteArrayOutputStream()
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
        if(!isBluetoothConnectionHandlerNull){
            Log.d("bluetooth","BluetoothConnectionHandler Is Not Null. Disconnecting...")
            disconnect()
        }
        bluetoothConnectionHandler= BluetoothConnectionHandler(bluetoothConnectionHandlerListener)
        heartBeatTimer.schedule(heartBeatTimerTask,INITIAL_MESSAGE_DELAY,Constants.HEARTBEAT_INTERVAL)
        validCheckTimer.schedule(validCheckTimerTask,INITIAL_MESSAGE_DELAY, Constants.VALIDCHECK_INTERVAL)
        isBluetoothConnectionHandlerNull=false
    }

    fun disconnect(){
        Log.d("bluetooth","disconnect...")
        try{
            if(!isBluetoothConnectionHandlerNull) {
                bluetoothConnectionHandler.close()
            }
        }catch(e:Exception){
            Log.d("bluetooth","disconnect failed : ${e.toString()}")
        }
        validCheckTimer.cancel()
        heartBeatTimer.cancel()
        isBluetoothConnectionHandlerNull=true
    }

    fun isValid():Boolean{
        if (!isBluetoothConnectionHandlerNull){
            if (commTimeInMillis==0L){
                return true
            }
            val diff : Long = Calendar.getInstance().timeInMillis -commTimeInMillis
            if(diff<Constants.COMMUNICATION_TIMEOUT){
                return true
            }
        }
        return false
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