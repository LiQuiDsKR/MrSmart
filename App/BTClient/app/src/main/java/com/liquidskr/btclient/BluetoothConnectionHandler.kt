package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.liquidskr.btclient.Constants.BLUETOOTH_MAX_CHUNK_LENGTH
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.UUID

class BluetoothConnectionHandler (
    private val listener: Listener,
    private val bluetoothDevice: BluetoothDevice
) : Thread() {

    var isConnected = false

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    private lateinit var bluetoothSocket: BluetoothSocket

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP (Serial Port Profile) UUID


    interface Listener {
        fun onConnected()
        fun onDisconnected()
        fun onDataArrived(datas: ByteArray)
        fun onDataSent(datas: ByteArray)
        fun onException(type:Constants.ExceptionType, description: String)
    }

    init {
        Log.d("bluetooth","ConnectionHandler created / device : $bluetoothDevice, uuid : $uuid")
        this.start()
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        isConnected = false

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket.connect()
            //Toast.makeText(context, "연결에 성공했습니다.", Toast.LENGTH_SHORT).show()+
            isConnected = true
        } catch (e: IOException) {
            isConnected = false
            Log.e("bluetooth", e.toString())
            // Toast.makeText(context, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
        }

        if (!isConnected) {
            listener.onException(Constants.ExceptionType.BLUETOOTH_CONNECTION_FAILED,"연결에 실패했습니다.")
            //Toast.makeText(context, "[${pcName}] 에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
        } else {
            listener.onConnected()
            try {
                inputStream = bluetoothSocket.inputStream
            } catch (e: IOException) {
                listener.onException(Constants.ExceptionType.BLUETOOTH_IO_EXCEPTION, e.toString())
            } catch (e: Exception) {
                listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
            }
        }
        // i/o stream을 설정합니다.

        try {
            while (isConnected) {
                val dataSize = inputStream.available()
                if (dataSize > 0) {
                    //데이터를 받은 경우.
                    val receivedData = ByteArray(dataSize)
                    inputStream.read(receivedData, 0, dataSize)
                    listener.onDataArrived(receivedData) //post로 처리하면 안됨.
                } else if (dataSize == 0) {
                    //받은 데이터가 없는 경우.
                    continue
                } else {
                    //연결이 끊긴 경우 : dataSize == -1
                    isConnected = false
                    Log.e("bluetooth", "Disconnected")
                    /*
                    //Toast.makeText(context, "블루투스 연결이 끊겼습니다. 다시 연결해주세요.", Toast.LENGTH_SHORT).show()
                    handler.post {
                        listener.onException(Constants.ExceptionType.BLUETOOTH_DISCONNECTED,"Disconnected.")
                    }
                    // finally에서 처리하기 때문에 주석처리함.
                    // post로 해야 하는지는 의문이다.
                     */
                }
            }
        } catch(e:IOException){
            listener.onException(Constants.ExceptionType.BLUETOOTH_IO_EXCEPTION,e.toString())
        } catch(e: Exception) {
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
        } finally {
            Log.d("bluetooth","thread end")
            try{inputStream.close()}catch(e:Exception){}finally {Log.d("bluetooth","InputStream Closed")}
            try{outputStream.close()}catch(e:Exception){}finally {Log.d("bluetooth","OutputStream Closed")}
            try{bluetoothSocket.close()}catch(e:Exception){}finally {Log.d("bluetooth","Socket Closed")}
            listener.onDisconnected()
        }
    }
    fun send(datas:ByteArray){
        /*
        if (outputStream == null){
            throw IllegalArgumentException("OutputStream cannot be null")
        }
        outputStream.write(datas);
        outputStream.flush();
        Log.d("bluetooth","dataSent : size = ${datas.size}");
        listener.onDataSent(datas);
        */
        try {
            outputStream = bluetoothSocket.outputStream

            var buffer = ByteBuffer.allocate(BLUETOOTH_MAX_CHUNK_LENGTH)
            var chunkSize = 0
            var offset = 0
            while (offset < datas.size) {
                chunkSize = minOf(buffer.remaining(), datas.size - offset)
                buffer.put(datas, offset, chunkSize)
                offset += chunkSize

                if (!buffer.hasRemaining() || offset >= datas.size) {
                    buffer.flip()
                    val byteArray = ByteArray(buffer.remaining())
                    buffer.get(byteArray)
                    outputStream.write(byteArray)
                    outputStream.flush()
                    Log.v("bluetooth_Send", byteArrayToHex(buffer.array()))
                    buffer.clear()
                }
            }
            listener.onDataSent(datas)
        } catch (e: IOException) {
            close()
            listener.onException(Constants.ExceptionType.BLUETOOTH_IO_EXCEPTION,e.toString())
        } catch (e: Exception) {
            close()
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION,e.toString())
        }
    }

    fun close(){
        Log.i("bluetooth","Conn : disconnecting...")
        isConnected=false
        this.interrupt()
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