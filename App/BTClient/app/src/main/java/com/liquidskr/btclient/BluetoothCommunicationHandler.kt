package com.liquidskr.btclient

import PermissionManager
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.database.sqlite.SQLiteException
import android.opengl.ETC1.isValid
import android.util.Log
import androidx.core.app.PendingIntentCompat.send
import com.liquidskr.btclient.Constants.BLUETOOTH_MAX_RECONNECT_ATTEMPT
import com.liquidskr.btclient.Constants.BLUETOOTH_RECONNECT_INTERVAL
import com.liquidskr.btclient.Constants.INITIAL_MESSAGE_DELAY
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

/**
 * 1-1. BluetoothDevice 정보 초기화
 * 1-2. 초기화된 정보를 주입하여 BluetoothConnectionHandler를 생성
 * 1-3. ConnectionHandler의 이벤트 리스너 보유
 *
 * 2-1. BluetoothMessageParser를 생성
 * 2-2. BluetoothMessageParser의 이벤트 리스너 보유
 *
 */
@SuppressLint("MissingPermission")//아니 분명히 권한 체크를 했는데도 지혼자 막 안했다고 뭐라해요 자꾸
class BluetoothCommunicationHandler (
    private val listener : Listener
){

    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onReconnectStarted()
        fun onReconnected()
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
            Log.d("bluetooth", "블루투스 연결에 성공했습니다")
            reconnectAttempt=0
            listener.onConnected()
        }

        override fun onDisconnected() {
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
    private var isInitialized : Boolean = false

    private var reconnectAttempt:Int =0
    private var commTimeInMillis : Long = 0L


    private val validCheckTimer = Timer()
    private val heartBeatTimer = Timer()
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
            if (isBluetoothConnectionHandlerNull) return
            val now = Calendar.getInstance().timeInMillis
            Log.d("bluetooth", "HeartBeat set : $now")
            send(Constants.BluetoothMessageType.HI.name + ",${Calendar.getInstance().timeInMillis}")
        }
    }

    var pcName: String = "정비실PC이름"
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice

    init {
        if (initialize()){
            connect()
            isBluetoothConnectionHandlerNull=false
        } else{
            listener.onException(Constants.ExceptionType.BLUETOOTH_NO_PAIRED_DEVICE,"기기[${pcName}]를 찾을 수 없습니다.")
            isBluetoothConnectionHandlerNull=true
        }
        //ConnectionHandler를 생성합니다.

    }
    private fun initialize(): Boolean {
        if (isInitialized) return true

        Log.d("bluetooth","Bluetooth initializing...")

        try {
            val dbHelper = DatabaseHelper.getInstance()
            pcName = dbHelper.getDeviceName()
            /*
            } catch (e: DatabaseHelper.DatabaseHelperInitializationException){
                listener.onException(Constants.ExceptionType.DATABASE_HELPER_NOT_INITIALIZED,"DatabaseHelper가 초기화되지 않았습니다.")
            } catch (e: SQLiteException){
                listener.onException(Constants.ExceptionType.SQLITE_EXCEPTION,"SQL QUERY ERROR!")
            } catch (e: SecurityException) {
                listener.onException(Constants.ExceptionType.EXTERNAL_DATABASE_PERMISSION_MISSING,"데이터베이스 접근 권한이 설정되지 않았습니다.")
             */
        } catch (e: Exception){
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
        }
        // 로컬 db에서 연결할 장치 이름을 확인합니다.

        val permissionManager : PermissionManager
        var pairedDevices : Set<BluetoothDevice> = emptySet()
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            permissionManager = PermissionManager
            permissionManager.checkAndRequestBluetoothPermissions()
            pairedDevices = bluetoothAdapter.bondedDevices
        } catch(e: Exception) {
            //분기 더 나눕시다
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
        }
        // 권한 확인 후 페어링된 기기 목록을 가져옵니다.

        if (pairedDevices.isEmpty()){
            listener.onException(Constants.ExceptionType.BLUETOOTH_NO_PAIRED_DEVICE,"페어링된 기기가 없습니다.")
        }else {
            for (device in pairedDevices) {
                if (device.name == pcName) {
                    bluetoothDevice = device
                    isInitialized=true
                    return true
                }
            }
        }
        // 페어링된 기기 목록에서 기기 이름과 맞는 device Mac주소를 가져옵니다.

        return false
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
        if(!isBluetoothConnectionHandlerNull){
            Log.d("bluetooth","BluetoothConnectionHandler Is Not Null. Disconnecting...")
            disconnect()
        }
        bluetoothConnectionHandler= BluetoothConnectionHandler(bluetoothConnectionHandlerListener,bluetoothDevice)
        //heartBeatTimer.schedule(heartBeatTimerTask,INITIAL_MESSAGE_DELAY,Constants.HEARTBEAT_INTERVAL)
        //validCheckTimer.schedule(validCheckTimerTask,INITIAL_MESSAGE_DELAY, Constants.VALIDCHECK_INTERVAL)
        isBluetoothConnectionHandlerNull=false
    }

    fun disconnect(){
        Log.d("bluetooth","Comm : disconnecting...")
        try{
            if(!isBluetoothConnectionHandlerNull){
                bluetoothConnectionHandler.close()
            }else{
                reconnect()
            }
        }catch(e:Exception){
            Log.d("bluetooth","disconnect failed : ${e.toString()}")
        }
        isBluetoothConnectionHandlerNull=true
    }
    fun reconnect(){
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

        try {
            Thread.sleep(Constants.BLUETOOTH_RECONNECT_INTERVAL)
        } catch (e: InterruptedException) {}
        //listener.onReconnectTry(reconnectAttempt)

        if(!isInitialized){
            if(initialize()){
                isBluetoothConnectionHandlerNull=false
            } else {
                listener.onException(Constants.ExceptionType.BLUETOOTH_NO_PAIRED_DEVICE,"기기[${pcName}]를 찾을 수 없습니다.")
                isBluetoothConnectionHandlerNull = true
            }
        } else {
            connect()
        }
    }

    fun isValid():Boolean{
        Log.d("bluetooth","isNull:$isBluetoothConnectionHandlerNull,cmTime:$commTimeInMillis,isInit:$isInitialized")
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