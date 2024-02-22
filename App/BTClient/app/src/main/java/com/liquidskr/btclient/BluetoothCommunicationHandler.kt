package com.liquidskr.btclient

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar

class BluetoothCommunicationHandler (
    private val listener : Listener
){

    interface Listener {
        fun onConnected()
        fun onDataArrived(data: String)
        fun onDataSent(data: String)
        fun onException(type:Constants.BluetoothExceptionType, description: String)
    }

    private val bluetoothMessageParserlistener : BluetoothMessageParser.Listener = object : BluetoothMessageParser.Listener {
        override fun onDataArrived(data: ByteArray) {
            listener.onDataArrived(String(data,Charsets.UTF_8))
            commTimeInMillis=0L
        }
        override fun onException(type: Constants.BluetoothExceptionType, description: String) {
            bluetoothConnectionHandler.close()
            listener.onException(type,description)
        }
    }
    private val bluetoothMessageParser : BluetoothMessageParser = BluetoothMessageParser(bluetoothMessageParserlistener)

    private val bluetoothConnectionHandlerListener :BluetoothConnectionHandler.Listener = object :BluetoothConnectionHandler.Listener{
        override fun onConnected() {
            Log.d("bluetooth", "블루투스 연결에 성공했습니다")
            listener.onConnected()
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

        override fun onException(type: Constants.BluetoothExceptionType, description: String) {
                listener.onException(type,description)
            }
        }
    private var bluetoothConnectionHandler : BluetoothConnectionHandler = BluetoothConnectionHandler(bluetoothConnectionHandlerListener)
    // ** var ▲ **
    private var isBluetoothConnectionHandlerNull:Boolean=false


    private var commTimeInMillis : Long = 0L

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

    fun disconnect(){
        Log.d("bluetooth","disconnect...")
        if(!isBluetoothConnectionHandlerNull){
            bluetoothConnectionHandler.close()
        }
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