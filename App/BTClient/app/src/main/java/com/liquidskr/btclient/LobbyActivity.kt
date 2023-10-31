package com.liquidskr.btclient

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction

class LobbyActivity  : AppCompatActivity() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var dbSyncBtn: ImageButton
    lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        bluetoothManager = BluetoothManager(this, this)

        workerBtn = findViewById(R.id.workerBtn)
        managerBtn = findViewById(R.id.managerBtn)
        dbSyncBtn = findViewById(R.id.DBSyncBtn)

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
            bluetoothManager.init()
            bluetoothManager.bluetoothOpen()
            bluetoothManager.dataReceive()
        }

    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
