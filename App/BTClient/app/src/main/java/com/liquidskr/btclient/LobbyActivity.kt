package com.liquidskr.btclient

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.liquidskr.fragment.ManagerFragment
import com.liquidskr.fragment.ManagerRentalFragment
import com.liquidskr.fragment.ManagerReturnFragment
import com.liquidskr.fragment.ManagerSelfRentalFragment
import com.liquidskr.fragment.WorkerFragment
import com.liquidskr.fragment.WorkerRentalFragment

class LobbyActivity  : AppCompatActivity() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var dbSyncBtn: ImageButton
    lateinit var testSendBtn: ImageButton
    lateinit var bluetoothBtn: ImageButton
    lateinit var settingBtn: ImageButton
    lateinit var bluetoothManager: BluetoothManager
    lateinit var managerSelfRentalFragment: ManagerSelfRentalFragment
    lateinit var managerRentalFragment: ManagerRentalFragment
    lateinit var managerReturnFragment: ManagerReturnFragment
    lateinit var workerRentalFragment: WorkerRentalFragment
    private var workerFragment: WorkerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        bluetoothManager = BluetoothManager(this, this)
        managerRentalFragment = ManagerRentalFragment()
        managerReturnFragment = ManagerReturnFragment()
        workerRentalFragment = WorkerRentalFragment()

        workerBtn = findViewById(R.id.workerBtn)
        managerBtn = findViewById(R.id.managerBtn)
        dbSyncBtn = findViewById(R.id.DBSyncBtn)
        testSendBtn = findViewById(R.id.testSendBtn)
        bluetoothBtn = findViewById(R.id.bluetoothBtn)
        settingBtn = findViewById(R.id.SettingBtn)

        workerBtn.setOnClickListener {
            val fragment = WorkerFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        managerBtn.setOnClickListener {
            val fragment = ManagerFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        dbSyncBtn.setOnClickListener {
            bluetoothManager.stopThread = false // 스레드 시작 시 변수 초기화
            bluetoothManager.dataReceiveSingleAndInsertDB()
        }
        testSendBtn.setOnClickListener {
            bluetoothManager.dataSend("REQUEST_StandardDB")
        }
        bluetoothBtn.setOnClickListener {
            bluetoothManager.bluetoothOpen()
        }
        settingBtn.setOnClickListener {

        }

    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else if (workerFragment != null) {
            workerFragment?.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth 권한이 허용됨
                // 여기에 Bluetooth 작업을 수행
            } else {
                // Bluetooth 권한이 거부됨
                // 권한 요청에 대한 사용자의 응답 처리
            }
        }
    }
}
