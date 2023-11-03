package com.liquidskr.btclient

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.provider.MediaStore.Audio.Genres.Members
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

import com.google.zxing.integration.android.IntentIntegrator
import com.mrsmart.standard.PageData
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.message.Message
import com.mrsmart.standard.tool.Tool


class BluetoothManager (private val context: Context, private val activity: Activity) {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var dataSet: String
    var dataEndFlag = false

    var gson = Gson()

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
        dataSet = ""
        inputStream = bluetoothSocket.inputStream
        while(!dataEndFlag) {
            val buffer = ByteArray(1024 * 64)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            if (receivedMessage.equals("RESPONSE_FINISH")) {
                dataEndFlag = true
                Log.d("DEBUGOKAY", "Ready to InsertData(Dataset)")
                Log.d("DEBUGOKAY", "dataset size: " + dataSet.length)
                Log.d("DEBUGOKAY", "dataset: " + dataSet)
                insertData(dataSet)
            } else {
                dataSet += receivedMessage
            }
        }
        dataSet = ""
        dataEndFlag = false
        inputStream = bluetoothSocket.inputStream
        while(!dataEndFlag) {
            val buffer = ByteArray(1024 * 64)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            if (receivedMessage.equals("RESPONSE_FINISH")) {
                dataEndFlag = true
                Log.d("DEBUGOKAY", "Ready to InsertData(Dataset)")
                Log.d("DEBUGOKAY", "dataset size: " + dataSet.length)
                Log.d("DEBUGOKAY", "dataset: " + dataSet)
                insertData(dataSet)
            } else {
                dataSet += receivedMessage
            }
        }
        bluetoothSocket.close()
    }
    private fun insertData(dataSet: String) {
        val dbHelper = DatabaseHelper(context)
        val message: Message = gson.fromJson(dataSet, Message::class.java)
        Log.d("INSERT_MEMBERSHIP", "TYPE : " + message.type)
        when (message.type) {
            1 -> { // if type == 1, REQUEST_MEMBER_LIST
                val listMembershipType = object : TypeToken<List<Membership>>(){}.type // listMembershipType에 List<Membership> 객체 저장
                val membershipList: List<Membership> = gson.fromJson(gson.toJson(message.content), listMembershipType)

                for (member in membershipList) {
                    val id = member.id
                    val code = member.code
                    val password = member.password
                    val name = member.name
                    val part = member.partDto.name
                    val subPart = member.partDto.subPartDto.name
                    val mainPart = member.partDto.subPartDto.mainPartDto.name
                    val role = member.role.toString()
                    val employmentStatus = member.employmentStatus.toString()
                    dbHelper.insertMembershipData(id, code, password, name, part, subPart, mainPart, role, employmentStatus)
                    Log.d("INSERT_MEMBERSHIP", "INSERT_MEMBERSHIP OK")
                }
            }
            2 -> { // if type == 2, REQUEST_TOOL_LIST
                val listToolType = object : TypeToken<List<Tool>>(){}.type // listMembershipType에 List<Tool> 객체 저장
                val toolList: List<Tool> = gson.fromJson(gson.toJson(message.content), listToolType)

                for (tool in toolList) {
                    val id = tool.id
                    val mainGroup = tool.subGroupDto.mainGroupDto.name
                    val subGroup = tool.subGroupDto.name
                    val code = tool.code
                    val krName = tool.name
                    val engName = tool.engName
                    val spec = tool.spec
                    val unit = tool.unit
                    val price = tool.price
                    val replacementCycle = tool.replacementCycle
                    //val buyCode = tool.buyCode
                    dbHelper.insertToolData(id, mainGroup, subGroup, code, krName, engName, spec, unit, price, replacementCycle)
                    //dbHelper.insertToolData(id, mainGroup, subGroup, code, krName, engName, spec, unit, price, replacementCycle, buyCode)
                    Log.d("INSERT_TOOL", "INSERT_TOOL OK")
                }
            }
            else -> {
                Log.d("Error_UnexpectedType", "예정되지 않은 type입니다.")
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
                dbHelper.updateToolData(toolId, toolMaingroup, toolSubgroup, toolCode, toolKrName, toolEngName, toolSpec, toolUnit, toolPrice, toolReplacementCycle)
                //dbHelper.updateToolData(toolId, toolMaingroup, toolSubgroup, toolCode, toolKrName, toolEngName, toolSpec, toolUnit, toolPrice, toolReplacementCycle, toolBuyCode)
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