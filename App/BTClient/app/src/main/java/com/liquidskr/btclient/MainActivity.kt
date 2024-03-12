package com.liquidskr.btclient

import SharedViewModel
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.fragment.LobbyFragment
import com.liquidskr.fragment.ReconnectFragment
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    @Deprecated("old")
    lateinit var bluetoothManagerOld: BluetoothManager_Old

    val bluetoothManager : BluetoothManager by lazy { BluetoothManager(
        Handler(Looper.getMainLooper())
    ) }
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }

    private lateinit var missingPermissions:List<String>
    private var bluetoothDevice : BluetoothDevice? = null
    private var allPermissionsGranted : Boolean = false
    private var isBluetoothEnabled : Boolean = false
    private val androidBluetoothManager: android.bluetooth.BluetoothManager by lazy {getSystemService(android.bluetooth.BluetoothManager::class.java)}
    private val bluetoothAdapter: BluetoothAdapter by lazy {androidBluetoothManager.adapter}

    private var accumulatedInput = StringBuilder()

    interface BluetoothModalListener {
        fun onConfirmButtonClicked()
        fun onCancelButtonClicked()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        val dbHelper = DatabaseHelper.initInstance(this)
        DialogUtils.initialize(this)

        checkPermission()
        requestPermission()

        //sharedViewModel.toolBoxId = dbHelper.getToolboxId()

        bluetoothManagerOld = BluetoothManager_Old(this, this)

        val mainFragment = LobbyFragment()
        val connectFragment = ReconnectFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, mainFragment)
            .addToBackStack(null)
            .commit()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, connectFragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager.stopTimer()
        bluetoothManager.disconnect()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.v("key","action:[${event.action}],keycode:[${event.keyCode}],unicode:[${event.unicodeChar}]")
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    supportFragmentManager.findFragmentById(R.id.fragmentContainer)?.let { fragment ->
                        if (fragment is InputProcessor) {
                            fragment.processInput(accumulatedInput.toString())
                            accumulatedInput.clear()
                            return true;
                        }
                    }
                }
                else -> {
                    val char = event.unicodeChar.toChar()
                    if (char.isLetterOrDigit() || char.isWhitespace()) {
                        Log.d("key","[${accumulatedInput}] detected")
                        accumulatedInput.append(char)
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }


    // 1. 필요한 권한이 허용되어 있는지 체크 -> 허용되지 않은 권한은 missingPermissions에 저장.
    private fun checkPermission(){
        val requiredPermissions : MutableList<String> = mutableListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12(API 레벨 31) 이상에서 추가된 블루투스 권한
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }else {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            requiredPermissions.add(Manifest.permission.BLUETOOTH)
                requiredPermissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    // 2. 미허용된 권한에 대해서 허용 요청.
    fun requestPermission(){
        if (missingPermissions.isNotEmpty()) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                allPermissionsGranted = permissions.entries.all { it.value }
                if (!allPermissionsGranted) {
                    Log.d(
                        "bluetooth",
                        "permission missing : " + permissions.filter { it.value }.toString()
                    )

                    Log.d("bluetooth", missingPermissions.toString())
                    DialogUtils.showAlertDialog(
                        "권한 허용 필요",
                        "앱을 사용하는 데 필수적인 권한이 허용되지 않았습니다. 설정에서 권한을 허용해주세요.",
                        "설정으로 이동",
                        "취소",
                        { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        },
                        { _, _ ->
                            DialogUtils.showAlertDialog(
                                "권한 허용 필요",
                                "앱을 사용하는 데 필수적인 권한이 허용되지 않았습니다. 앱을 종료합니다."
                            ) { _, _ ->
                                finish()
                            }
                        }
                    )
                }else{
                    enableBluetooth()
                }
            }
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        } else{
            allPermissionsGranted=true
            enableBluetooth()
        }
    }

    // 3. 블루투스 기능 활성화
    fun enableBluetooth(){
        if(!allPermissionsGranted) return
        // 블루투스 활성화 요청 인텐트
        val bluetoothEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val enableBluetooth :ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("bluetooth",result.toString())
            isBluetoothEnabled = (result.resultCode == RESULT_OK)
            if (result.resultCode == RESULT_OK){
                getPairedDevice()
            } else {
                Log.d("bluetooth","Bluetooth Device Search Failed : Bluetooth Not Enabled")
                DialogUtils.showAlertDialog("블루투스 비활성화됨","블루투스가 비활성화되어 있습니다. 블루투스를 활성화한 후, 앱을 다시 실행해주세요."){
                        _,_-> finish()
                }
            }
        }
        enableBluetooth.launch(bluetoothEnableIntent)
    }

    // 4. 페어링된 장비 정보 불러오기.
    @Deprecated("아래 getPairedDevice() 사용할 것.")
    fun getPairedDeviceWithName() {
        Log.d("bluetooth","Bluetooth initializing...")
        var pcName = "DESKTOP"
        try {
            //로컬 db에 저장되어 있는 연결할 PC Name을 불러옵니다.
            val dbHelper = DatabaseHelper.getInstance()

            val bluetoothDeviceSaveService = BluetoothDeviceSaveService.getInstance()
            pcName = dbHelper.getDeviceName()
            if (pcName.length<1){
                val currentPCName = bluetoothDeviceSaveService.getPCName()?:""
                val callback: (String) -> Unit = { text ->
                    bluetoothDeviceSaveService.insertPCName(text)
                    pcName=text
                }
                DialogUtils.showTextDialog("정비실 노트북(PC)의 이름을 입력하세요.", currentPCName, callback)
            }

            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                // 페어링된(bonded) 기기 목록을 가져옵니다.
                val pairedDevices = bluetoothAdapter.bondedDevices
                if (pairedDevices.isNotEmpty()) {
                    for (device : BluetoothDevice in pairedDevices){
                        val deviceName = device.name
                        val deviceAddress = device.address // MAC 주소
                        Log.d("bluetooth","Paired Device: Name: $deviceName, Address: $deviceAddress")
                        if (deviceName==pcName){
                            this.bluetoothManager.setDevice(device)
                            return
                        }else{
                            continue
                        }
                    }
                }
                Log.d("bluetooth","기기[${pcName}]를 찾을 수 없습니다.")
            } else {
                // 블루투스가 비활성화되어 있거나, 지원하지 않는 경우의 처리 로직
                Log.d("bluetooth","Exception on Initializing")
            }
        } catch(e: Exception) {
            Log.e("bluetooth",e.toString())
        }
        DialogUtils.showAlertDialog("페어링 없음", "기기[${pcName}]를 찾을 수 없습니다. 앱을 종료합니다.") {_,_->finish()}
    }

    // 4. 페어링된 장비 정보 불러오기.
    fun getPairedDevice() {
        if (!isBluetoothEnabled) {
            Log.d("bluetooth","Bluetooth Device Search Failed : Bluetooth Not Enabled")
            DialogUtils.showAlertDialog("블루투스 비활성화됨","블루투스가 비활성화되어 있습니다. 블루투스를 활성화한 후, 앱을 다시 실행해주세요."){
                    _,_-> finish()
            }
            return
        }
        Log.d("bluetooth","Bluetooth Device Search Start")
        var pcName = "DESKTOP"
        try {
            //로컬 db에 저장되어 있는 연결할 PC Name을 불러옵니다.
            val dbHelper = DatabaseHelper.getInstance()

            val bluetoothDeviceSaveService = BluetoothDeviceSaveService.getInstance()
            pcName =bluetoothDeviceSaveService.getPCName()

            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                // 페어링된(bonded) 기기 목록을 가져옵니다.
                val pairedDevices = bluetoothAdapter.bondedDevices
                if (pairedDevices.isNotEmpty()) {
                    for (device : BluetoothDevice in pairedDevices){
                        val deviceName = device.name
                        val deviceAddress = device.address // MAC 주소
                        Log.d("bluetooth","Paired Device: Name: $deviceName, Address: $deviceAddress")
                        if (deviceAddress==pcName){
                            bluetoothDevice=device
                            this.bluetoothManager.setDevice(device)
                            bluetoothManager.startTimer()
                            return
                        }else{
                            continue
                        }
                    }
                }
                Log.d("bluetooth","기기[${pcName}]를 찾을 수 없습니다.")
                DialogUtils.showSingleChoiceDialog("정비실 노트북(PC)의 이름을 입력하세요.",
                    bluetoothAdapter.bondedDevices.map { device ->
                        "${device.type} : ${device.name} (${device.address})"
                    }.toTypedArray())
                { selectedIndex ->
                    val device = bluetoothAdapter.bondedDevices.toList()[selectedIndex]
                    Log.d("bluetooth","device index : $selectedIndex")
                    val deviceName = device.name
                    val deviceAddress = device.address // MAC 주소
                    Log.d("bluetooth", "Paired Device: Name: $deviceName, Address: $deviceAddress")

                    bluetoothDeviceSaveService.insertPCName(device.address)
                    bluetoothDevice = device
                    this.bluetoothManager.setDevice(device)

                    bluetoothManager.startTimer()
                }
            } else {
                // 블루투스가 비활성화되어 있거나, 지원하지 않는 경우의 처리 로직
                Log.d("bluetooth","Exception on Initializing")
            }
        } catch(e: Exception) {
            Log.e("bluetooth",e.toString())
        }
    }

    fun setBluetoothManagerListener(listener: BluetoothManager.Listener){
        bluetoothManager.listener=listener
    }

    @Deprecated("old")
    fun getBluetoothManagerOnActivity(): BluetoothManager_Old {
        return bluetoothManagerOld
    }
}
