package com.liquidskr.btclient

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.liquidskr.fragment.ManagerFragment
import com.liquidskr.fragment.UserListFragment
import com.liquidskr.fragment.WorkerFragment

class LobbyActivity  : AppCompatActivity() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var dbSyncBtn: ImageButton
    lateinit var bluetoothManager: BluetoothManager
    private var workerFragment: WorkerFragment? = null

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
        } else if (workerFragment != null) {
            workerFragment?.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
