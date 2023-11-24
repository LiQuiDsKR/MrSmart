package com.liquidskr.btclient

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.message.Message
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.tool.ToolDto
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class BluetoothManager (private val context: Context, private val activity: Activity) {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0
    private lateinit var dataSet: String

    private var timeoutHandler: Handler = Handler(Looper.getMainLooper())
    var dataEndFlag = false

    var gson = Gson()
    var dataCheckStep = 0
    var dataCheckSize = 0
    var timeout = false
    var totalMessage = ""

    var totalByteList = ArrayList<ByteArray>()

    var currentPercentage: Int = 0
    var totalPercentage: Int = 0

    val backgroundThread = Thread { // 쓰레드, 백그라운드 실행
        dataReceiveToWriteDB()
    }
    var stopThread = false // 쓰레드 종료 Flag

    val isCommunicating = false

    fun bluetoothOpen() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val permissionManager = PermissionManager(activity)
        var flagPermissionOK: Boolean = false
        permissionManager.checkAndRequestPermission()
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

    fun BackgroundThread() {
        inputStream = bluetoothSocket.inputStream
        backgroundThread.start()

    }
    fun stopBackgroundThread() {
        stopThread = true // 스레드 중지를 요청
    }

    fun dataReceiveToWriteDB() {
        val timeoutRunnable = Runnable { // 예상된 데이터보다 적은 데이터를 받아 Loop를 돌다 Timeout이 일어난 경우
            timeout = true
            dataSend("TIMEOUT")
        }
        while (!stopThread) {
            timeout = false // Timeout Flag 초기화
            if (inputStream.available() > 0) {
                when (dataCheckStep) {
                    0 -> { // 0단계, 전체 데이터의 크기를 미리 받고 체크하는 단계
                        val size = inputStream.available()
                        val buffer = ByteArray(size)
                        val readData = inputStream.read(buffer)
                        val receivedMessage = String(buffer, 0, readData)
                        dataSend(receivedMessage)
                        dataCheckStep = 1 // 받은 데이터의 크기를 그대로 반송하며 1단계 진입
                        dataCheckSize = receivedMessage.toInt()// 받은 데이터 저장
                    }

                    1 -> { // 1단계, 실제 데이터를 받는 단계
                        timeoutHandler.postDelayed(
                            timeoutRunnable,
                            10000
                        ) // 예상한 데이터 크기보다 적게들어올 경우 Timeout 처리
                        totalByteList.clear()
                        while (true) {
                            val size = inputStream.available()
                            val buffer = ByteArray(size)
                            val readData = inputStream.read(buffer)
                            val receivedByte = buffer.copyOf(readData)
                            totalByteList.add(receivedByte) // 받은 내용 누적

                            val byteListSize = String(combineByteArrays(totalByteList),Charsets.UTF_8).toByteArray().size
                            if (byteListSize == dataCheckSize) { // 예상된 데이터와 현재 받은 데이터의 크기가 일치한다면
                                dataSend("DATASIZE_OK") // 데이터 수신 정상, 사이클 종료
                                dataCheckStep = 0
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                totalMessage = String(combineByteArrays(totalByteList),Charsets.UTF_8)
                                insertData(totalMessage) // 받은 모든 메세지를 DB에 저장 (InsertData)
                                break
                            } else if (byteListSize > dataCheckSize) { // 아직 Timeout 전이지만 예상된 데이터보다 많이 받았을 경우
                                dataSend("DATASIZE_OVERFLOW") // 데이터 과수신, 2단계로 재진입 (정보 수신대기)
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                dataCheckStep = 1
                                break
                            } else if (byteListSize < dataCheckSize) {
                                dataSend("DATASIZE_UNDERFLOW")
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                dataCheckStep = 1
                                break
                            }
                            if (timeout) {
                                break
                            }
                        }
                        dataCheckStep = 0
                    }
                }
            }
            try {
                Thread.sleep(1000) // 100ms
            } catch (e: InterruptedException) {

            }
        }
    }
    fun dataReceive(): String {
        val timeoutRunnable = Runnable { // 예상된 데이터보다 적은 데이터를 받아 Loop를 돌다 Timeout이 일어난 경우
            timeout = true
            dataSend("TIMEOUT")
        }
        while (!stopThread) {
            timeout = false // Timeout Flag 초기화
            if (inputStream.available() > 0) {
                when (dataCheckStep) {
                    0 -> { // 0단계, 전체 데이터의 크기를 미리 받고 체크하는 단계
                        val size = inputStream.available()
                        val buffer = ByteArray(size)
                        val readData = inputStream.read(buffer)
                        val receivedMessage = String(buffer, 0, readData)
                        dataSend(receivedMessage)
                        dataCheckStep = 1 // 받은 데이터의 크기를 그대로 반송하며 1단계 진입
                        dataCheckSize = receivedMessage.toInt()// 받은 데이터 저장
                    }

                    1 -> { // 1단계, 실제 데이터를 받는 단계
                        timeoutHandler.postDelayed(
                            timeoutRunnable,
                            10000
                        ) // 예상한 데이터 크기보다 적게들어올 경우 Timeout 처리
                        totalByteList.clear()
                        while (true) {
                            val size = inputStream.available()
                            val buffer = ByteArray(size)
                            val readData = inputStream.read(buffer)
                            val receivedByte = buffer.copyOf(readData)
                            totalByteList.add(receivedByte) // 받은 내용 누적

                            val byteListSize = String(combineByteArrays(totalByteList),Charsets.UTF_8).toByteArray().size
                            if (byteListSize == dataCheckSize) { // 예상된 데이터와 현재 받은 데이터의 크기가 일치한다면
                                dataSend("DATASIZE_OK") // 데이터 수신 정상, 사이클 종료
                                dataCheckStep = 0
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                totalMessage = String(combineByteArrays(totalByteList),Charsets.UTF_8)
                                break
                            } else if (byteListSize > dataCheckSize) { // 아직 Timeout 전이지만 예상된 데이터보다 많이 받았을 경우
                                dataSend("DATASIZE_OVERFLOW") // 데이터 과수신, 2단계로 재진입 (정보 수신대기)
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                dataCheckStep = 1
                                break
                            } else if (byteListSize < dataCheckSize) {
                                dataSend("DATASIZE_UNDERFLOW")
                                timeoutHandler.removeCallbacks(timeoutRunnable) // timeout 일어나지 않게끔 Handler 제거
                                dataCheckStep = 1
                                break
                            }
                            if (timeout) {
                                break
                            }
                        }
                        dataCheckStep = 0
                    }
                }
            }
            try {
                Thread.sleep(1000) // 100ms
            } catch (e: InterruptedException) {

            }
        }

        return totalMessage
    }

    fun dataReceiveSingle(): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        val readData = inputStream.read(buffer)
        val receivedByte = buffer.copyOf(readData)
        return String(receivedByte)
    }

    fun combineByteArrays(byteArrays: List<ByteArray>): ByteArray {
        var totalLength = 0
        for (byteArray in byteArrays) {
            totalLength += byteArray.size
        }
        val resultByteArray = ByteArray(totalLength)
        var currentIndex = 0
        for (byteArray in byteArrays) {
            System.arraycopy(byteArray, 0, resultByteArray, currentIndex, byteArray.size)
            currentIndex += byteArray.size
        }

        return resultByteArray
    }
    private fun insertData(dataSet: String) {
        val dbHelper = DatabaseHelper(context)
        Log.d("test", dataSet)
        val message: Message = gson.fromJson(dataSet, Message::class.java)
        Log.d("INSERT_MEMBERSHIP", "TYPE : " + message.type)
        when (message.type) {
            1 -> { // if type == 1, REQUEST_MEMBER_LIST
                val pagedata: Page = gson.fromJson(gson.toJson(message.page), Page::class.java)
                val listMembershipType = object : TypeToken<List<Membership>>(){}.type
                val membershipList: List<Membership> = gson.fromJson(gson.toJson(pagedata.content), listMembershipType)
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
                val pagedata: Page = gson.fromJson(gson.toJson(message.page), Page::class.java)
                val listToolType = object : TypeToken<List<ToolDto>>(){}.type
                val toolList: List<ToolDto> = gson.fromJson(gson.toJson(pagedata.content), listToolType)

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
        Log.d("SEND", sendingData)
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