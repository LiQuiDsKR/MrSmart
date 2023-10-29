package com.liquidskr.btclient

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import com.google.zxing.integration.android.IntentIntegrator


class BluetoothActivity : AppCompatActivity() {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var testBtn: Button
    private lateinit var editText: EditText
    private lateinit var data: String
    private lateinit var QRcodeBtn: Button
    private lateinit var JsonField: TextView
    private lateinit var dbSyncBtn: Button
    private lateinit var btConnect: Button
    private lateinit var btClose: Button

    private val requestCode = 101

    private lateinit var dbHelper: DatabaseHelper

    lateinit var dataSet : String
    lateinit var jsonMainPart : String
    lateinit var jsonSubPart : String
    lateinit var jsonPart : String
    lateinit var jsonMembership : String
    lateinit var jsonMainGroup : String
    lateinit var jsonSubGroup : String
    lateinit var jsonTool : String
    var dataEndFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        dbHelper = DatabaseHelper(this)
        dataSet = ""
        // ============================= UI zone start ====================================
        editText = findViewById(R.id.edit_text)
        testBtn = findViewById(R.id.testBtn)
        QRcodeBtn = findViewById(R.id.QRcodeBtn)
        JsonField = findViewById(R.id.jsonField)
        dbSyncBtn = findViewById(R.id.dbSyncBtn)
        btConnect = findViewById(R.id.BTConnect)
        btClose = findViewById(R.id.BTClose)

        testBtn.setOnClickListener { view: View->

        }

        btConnect.setOnClickListener { view: View->
            bluetoothOpen()
        }
        btClose.setOnClickListener { view: View->
            bluetoothClose()
        }

        QRcodeBtn.setOnClickListener { view: View->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCode)
            } else {
                // Start QR code scanner
                startQRScanner()
            }
            startQRScanner()
        }

        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                try {
                    val tempText = editText.text.toString()
                    tempText.substring(0, tempText.length - 1)
                    data = tempText
                    dataSend(tempText)
                    data = ""
                    editText.setText("")
                } catch (e: StringIndexOutOfBoundsException) {
                    Toast.makeText(this,"빈 내용을 전송할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }


                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        dbSyncBtn.setOnClickListener {
            dataEndFlag = false
            while(!dataEndFlag) { // 데이터 수신이 끝나지 않았다면
                try {
                    dataReceive()
                } catch (e: UninitializedPropertyAccessException) {
                    Toast.makeText(this,"블루투스 연결이 없습니다.", Toast.LENGTH_SHORT).show()
                    break
                }
            }

        }
        // ============================= UI zone end ====================================
    }

    fun bluetoothOpen() {
        val permissionManager = PermissionManager(this)
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
            Toast.makeText(this,"IOException, read failed.\n소켓이 닫혔거나 Timeout 상태입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun bluetoothClose() {
        try {
            bluetoothSocket.close()
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(this,"블루투스 연결이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    fun dataReceive() {
        inputStream = bluetoothSocket.inputStream
        val buffer = ByteArray(1024 * 1024)
        val bytesRead = inputStream.read(buffer)
        val receivedMessage = String(buffer, 0, bytesRead)
        //JsonField.text = receivedMessage.length.toString() + ", " + receivedMessage // sizeCheck
        dataSet = dataSet + receivedMessage
        if (receivedMessage.equals("EndMembership")) {
            dataEndFlag = true
            insertMembership(dataSet)
            JsonField.text = "[" + dataSet.length.toString() + "] " + dataSet // size check
        }
    }

    fun insertMembership(dataSet: String) {
        val rows = dataSet.split("\n")
        val dbHelper = DatabaseHelper(this)

        for (row in rows) {
            val columns = row.split(",")
            if (columns.size == 6) {
                val code = columns[0].trim()
                val password = columns[1].trim()
                val name = columns[2].trim()
                val part = columns[3].trim()
                val role = columns[4].trim()
                val employmentState = columns[5].trim()
                dbHelper.insertData(code, password, name, part, role, employmentState)
            }
        }
        dbHelper.close()
    }

    fun dataSend(sendingData: String) {
        try {
            outputStream = bluetoothSocket.outputStream
            outputStream.write(sendingData.toByteArray())
            outputStream.flush()
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(this,"블루투스 연결이 없습니다.", Toast.LENGTH_SHORT).show()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket.close()
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }

    // 이 아래는 QR Camera 중 권한 요청 처리입니다.
    private fun startQRScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX)
        integrator.setPrompt("2D Data Matrix 인식 대기 중...")
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {

            if (result.contents != null) {
                dataSend(result.contents.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}