package com.liquidskr.btclient

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

import com.google.zxing.integration.android.IntentIntegrator



class BluetoothManager (private val context: Context, private val activity: Activity) {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var dataSet: String
    var dataEndFlag = false

    fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        dataSet = ""
    }

    fun bluetoothOpen() {
        val permissionManager = PermissionManager(activity)
        var flagPermissionOK: Boolean = false
        while(!flagPermissionOK) {
            permissionManager.checkAndRequestPermission(
                "android.permission.BLUETOOTH_CONNECT",{
                    val pairedDevices = bluetoothAdapter.bondedDevices
                    if (pairedDevices.size > 0) {
                        for (device in pairedDevices) {
                            if (device.name == "LQD") { // 연결하려는 디바이스의 이름을 지정하세요.
                                bluetoothDevice = device
                                break
                            }
                        }
                        flagPermissionOK = true
                    } else {
                        Toast.makeText(context, "연결된 기기가 없습니다.", Toast.LENGTH_SHORT).show()
                    }

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
        while(!dataEndFlag) {
            val buffer = ByteArray(1024 * 64)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            if (receivedMessage.equals("EndMembership")) {
                dataEndFlag = true
                insertMembership(dataSet)
            } else {
                dataSet += receivedMessage
            }
        }
        dataSet = ""
        dataEndFlag = false
        while(!dataEndFlag) {
            val buffer = ByteArray(1024 * 64)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            if (receivedMessage.equals("EndTool")) {
                dataEndFlag = true
                insertTool(dataSet)
            } else {
                dataSet += receivedMessage
            }
        }
        bluetoothSocket.close()
    }

    private fun insertMembership(dataSet: String) {
        val rows = dataSet.split("\n")
        val dbHelper = DatabaseHelper(context)

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 9) {
                val id = columns[0].trim().toLong()
                val code = columns[1].trim()
                val password = columns[2].trim()
                val name = columns[3].trim()
                val part = columns[4].trim()
                val subpart = columns[5].trim()
                val mainpart = columns[6].trim()
                val role = columns[7].trim()
                val employmentState = columns[8].trim()
                dbHelper.insertMembershipData(id, code, password, name, part, subpart, mainpart, role, employmentState)
            }
        }
        dbHelper.close()
    }

    fun insertTool(dataSet: String) {
        val rows = dataSet.split("\n")
        val dbHelper = DatabaseHelper(context)

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 11) {
                val toolId = columns[0].trim().toLong()
                val toolMaingroup = columns[1].trim()
                val toolSubgroup = columns[2].trim()
                val toolCode = columns[3].trim()
                val toolKrName = columns[4].trim()
                val toolEngName = columns[5].trim()
                val toolSpec = columns[6].trim()
                val toolUnit = columns[7].trim()
                val toolPrice = columns[8].trim().toInt()
                val toolReplacementCycle = columns[9].trim().toInt()
                val toolBuyCode = columns[10].trim()
                dbHelper.insertToolData(toolId, toolMaingroup, toolSubgroup, toolCode, toolKrName, toolEngName, toolSpec, toolUnit, toolPrice, toolReplacementCycle, toolBuyCode)
            }
        }
        dbHelper.close()
    }

    fun updateTool(dataSet: String) {
        val rows = dataSet.split("\n")
        val dbHelper = DatabaseHelper(context)

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 11) {
                val toolId = columns[0].trim().toLong()
                val toolMaingroup = columns[1].trim()
                val toolSubgroup = columns[2].trim()
                val toolCode = columns[3].trim()
                val toolKrName = columns[4].trim()
                val toolEngName = columns[5].trim()
                val toolSpec = columns[6].trim()
                val toolUnit = columns[7].trim()
                val toolPrice = columns[8].trim().toInt()
                val toolReplacementCycle = columns[9].trim().toInt()
                val toolBuyCode = columns[10].trim()
                dbHelper.updateToolData(toolId, toolMaingroup, toolSubgroup, toolCode, toolKrName, toolEngName, toolSpec, toolUnit, toolPrice, toolReplacementCycle, toolBuyCode)
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