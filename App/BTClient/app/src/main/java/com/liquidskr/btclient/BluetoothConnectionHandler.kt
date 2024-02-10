package com.liquidskr.btclient

import PermissionManager
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.UUID

class BluetoothConnectionHandler (
    private val listener: Listener?
) : Thread() {


    companion object {
        @Volatile //멀티스레딩 시 변수 선언 안전성을 위함.
        private var runningThreadCount = 0
    }

    private var socket: Socket? = null
    //private var inputStream: InputStream? = null
    private var bufferedInputStream: BufferedInputStream? = null
    //private var outputStream: OutputStream? = null
    private var bufferedOutputStream: BufferedOutputStream? = null
    //private var status: Int = 0
    private var isConnected = false

    //private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket

    //private lateinit var receiveThread: Thread
    private val handler = Handler(Looper.getMainLooper())

    //var isSending: Boolean = false
    //private val messageQueue: Queue<BluetoothMessage> = LinkedList()

    var pcName: String = "정비실PC이름"


    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onDataArrived(datas: ByteArray)
        fun onDataSent(size: Int)
        fun onException(type:Constansts.BluetoothExceptionType,description: String)
    }

    init {
        start()
    }

    @SuppressLint("MissingPermission")//아니 분명히 권한 체크를 했는데도 지혼자 막 안했다고 뭐라해요 자꾸
    override fun run() {
        try {
            val dbHelper = DatabaseHelper.getInstance()
            pcName = dbHelper.getDeviceName()
        } catch (e: DatabaseHelper.DatabaseHelperInitializationException){
            listener?.onException(Constansts.BluetoothExceptionType.DATABASE_HELPER_NOT_INITIALIZED,"DatabaseHelper가 초기화되지 않았습니다.")
        } catch (e: SQLiteException){
            listener?.onException(Constansts.BluetoothExceptionType.SQLITE_EXCEPTION,"SQL QUERY ERROR!")
        } catch (e: SecurityException) {
            listener?.onException(Constansts.BluetoothExceptionType.EXTERNAL_DATABASE_PERMISSION_MISSING,"데이터베이스 접근 권한이 설정되지 않았습니다.")
        } catch (e: Exception){
            listener?.onException(Constansts.BluetoothExceptionType.DEFAULT_EXCEPTION,e.toString())
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
            listener?.onException(Constansts.BluetoothExceptionType.DEFAULT_EXCEPTION,e.toString())
        }
        // 권한 확인 후 페어링된 기기 목록을 가져옵니다.

        if (pairedDevices.isEmpty()){
            listener?.onException(Constansts.BluetoothExceptionType.NO_PAIRED_DEVICE,"페어링된 기기가 없습니다.")
        }else{
            isConnected = false// 서버와 연결 부분
            for (device in pairedDevices) {
                if (device.name == pcName) {
                    val bluetoothDevice = device
                    val uuid =
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP (Serial Port Profile) UUID
                    try {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
                        bluetoothSocket.connect()
                        //Toast.makeText(context, "연결에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        isConnected = true
                    } catch (e: IOException) {
                        isConnected = false
                        Log.d("bluetooth", e.toString())

                        // Toast.makeText(context, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    break
                }
            }
            if (!isConnected) {
                listener?.onException(Constansts.BluetoothExceptionType.CONNECTION_FAILED,"[${pcName}]와의 연결에 실패했습니다.")
                //Toast.makeText(context, "[${pcName}] 에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        //페어링된 기기 목록 중 해당 정비실의 기기에 연결 시도합니다.

        try {
            inputStream = bluetoothSocket.inputStream
            outputStream = ByteArrayOutputStream() // 최종 byteStream
        } catch (e : Exception){
            listener?.onException(Constansts.BluetoothExceptionType.DEFAULT_EXCEPTION,e.toString())
        }


        while (bluetoothSocket.isConnected) {

            var dataSize = -1
            val size = inputStream.available()
            if (size > 0) {
                val readDatas = ByteArray(size)
                inputStream.read(readDatas, 0, size)
                outputStream.write(readDatas)

            } else if (size == 0) {
                continue
            } else {
                isConnected = false
                Log.d("bluetooth_", "Disconnected")
                //Toast.makeText(context, "블루투스 연결이 끊겼습니다. 다시 연결해주세요.", Toast.LENGTH_SHORT).show()
                handler.post {
                    listener?.onException(Constansts.BluetoothExceptionType.DISCONNECTED,"블루투스 연결이 끊겼습니다.")
                }
            }
        }
    }
    fun send(datas:ByteArray){
        if (outputStream == null){
            throw Exception("mOutputStream is null...네트워크 연결을 확인하세요.");
        }

        bufferedOutputStream?.write(datas);
        bufferedOutputStream?.flush();
        Log.d("bluetooth","dataSent : size = ${datas.size}");
        listener?.onDataSent(datas.size);
    }

    fun close(){
        this.interrupt();
    }
}