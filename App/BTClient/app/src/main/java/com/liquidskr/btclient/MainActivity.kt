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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import com.google.zxing.integration.android.IntentIntegrator


class MainActivity : AppCompatActivity() {
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream


    private lateinit var testBtn: Button
    private lateinit var editText: EditText
    private lateinit var data: String
    private lateinit var QRcodeBtn: Button

    private lateinit var JsonField: TextView
    private lateinit var dbSyncBtn: Button

    private val requestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.edit_text)
        testBtn = findViewById(R.id.testBtn)
        QRcodeBtn = findViewById(R.id.QRcodeBtn)
        JsonField = findViewById(R.id.jsonField)
        dbSyncBtn = findViewById(R.id.dbSyncBtn)

        testBtn.setOnClickListener { view: View->
            onSend("TestButton")
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
                // Enter 키를 누르면 실행할 작업을 여기에 추가
                val tempText = editText.text.toString()
                tempText.substring(0, tempText.length - 1)
                data = tempText
                onSend(tempText)
                data = ""
                editText.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        dbSyncBtn.setOnClickListener {
            onSend("request_DB")

            try {
                dataReceive()
            } catch (e: IOException) {
                Log.e("Error", e.toString())
            }
        }
    }

    fun dataReceive() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
            inputStream = bluetoothSocket.inputStream

            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            val receivedMessage = String(buffer, 0, bytesRead)
            JsonField.text = receivedMessage;

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            bluetoothSocket.close()
        }


    }

    fun onSend(sendingData: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
            outputStream = bluetoothSocket.outputStream

            val dataToSend = sendingData

            outputStream.write(dataToSend.toByteArray())
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            bluetoothSocket.close()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket.close()
        } catch (e: IOException) {
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
                onSend(result.contents.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}