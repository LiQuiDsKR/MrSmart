package com.liquidskr.btclient

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

import com.google.zxing.integration.android.IntentIntegrator



class BluetoothManager (private val context: Context){
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var dataSet: String
    private lateinit var data: String
    var dataEndFlag = false

    fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        dbHelper = DatabaseHelper(context)
        dataSet = ""
    }

    fun bluetoothOpen() {
        val permissionManager = PermissionManager(LobbyActivity())
        var flagPermissionOK: Boolean = false
        while(!flagPermissionOK) {
            permissionManager.checkAndRequestPermission(
                "android.permission.BLUETOOTH_CONNECT",{
                    val pairedDevices = bluetoothAdapter.bondedDevices
                    for (device in pairedDevices) {
                        if (device.name == "LQD") { // 연결하려는 디바이스의 이름을 지정하세요.
                            bluetoothDevice = device
                            break
                        }
                    }
                    flagPermissionOK = true
                },
                {
                    flagPermissionOK = false
                }
            )
        }
        try {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP (Serial Port Profile) UUID
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket.connect()


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun bluetoothClose() {
        bluetoothSocket.close()
    }
    fun dataReceive() {
        inputStream = bluetoothSocket.inputStream
        val buffer = ByteArray(1024 * 1024)
        val bytesRead = inputStream.read(buffer)
        val receivedMessage = String(buffer, 0, bytesRead)
        dataSet = dataSet + receivedMessage
        if (receivedMessage.equals("EndMembership")) {
            dataEndFlag = true
            insertMembership(dataSet)
            //JsonField.text = dataSet.length.toString() // size check
        }
    }

    fun insertMembership(dataSet: String) {
        val rows = dataSet.split("\n")
        val dbHelper = DatabaseHelper(context)

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 6) {
                val code = columns[0].trim()
                val password = columns[1].trim()
                val name = columns[2].trim()
                val part = columns[3].trim()
                val role = columns[4].trim() // 정수로 변환
                val employmentState = columns[5].trim() // 정수로 변환
                dbHelper.insertData(code, password, name, part, role, employmentState)
            }
        }
        dbHelper.close()
    }

    fun dataSend(sendingData: String) {
        outputStream = bluetoothSocket.outputStream
        outputStream.write(sendingData.toByteArray())
        outputStream.flush()
    }

    // 이 아래는 QR Camera 중 권한 요청 처리입니다.
    private fun startQRScanner() {
        val integrator = IntentIntegrator(LobbyActivity())
        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX)
        integrator.setPrompt("2D Data Matrix 인식 대기 중...")
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }
}