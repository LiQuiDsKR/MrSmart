package com.liquidskr.btclient

import PermissionManager
import SharedViewModel
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.Constants.REQUEST_CODE
import com.liquidskr.fragment.LobbyFragment


class MainActivity : AppCompatActivity() {

    @Deprecated("old")
    lateinit var bluetoothManagerOld: BluetoothManager_Old

    private val bluetoothManager : BluetoothManager = BluetoothManager(Handler(Looper.getMainLooper()))

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }

    interface CustomModalListener {
        fun onConfirmButtonClicked()
        fun onCancelButtonClicked()
    }
    interface AlertModalListener {
        fun onConfirmButtonClicked()
        fun onCancelButtonClicked()
    }
    interface BluetoothModalListener {
        fun onConfirmButtonClicked()
        fun onCancelButtonClicked()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        val dbHelper = DatabaseHelper.initInstance(this)
        val permissionManager = PermissionManager
        permissionManager.initialize(this)

        sharedViewModel.toolBoxId = dbHelper.getToolboxName()

        //val handler = Handler(Looper.getMainLooper())

        bluetoothManagerOld = BluetoothManager_Old(this, this)

        val fragment = LobbyFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager.disconnect()
    }


    fun showBluetoothModal(title: String, content: String, listener: BluetoothModalListener) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(content)

        // 확인 버튼을 눌렀을 때의 동작 정의
        builder.setPositiveButton("확인") { _, _ ->
            // 모달 확인 버튼을 눌렀을 때, Listener의 메서드 호출
            listener.onConfirmButtonClicked()
        }

        // 취소 버튼을 눌렀을 때의 동작 정의
        builder.setNegativeButton("취소") { _, _ ->
            // 모달 취소 버튼을 눌렀을 때, Listener의 메서드 호출
            listener.onCancelButtonClicked()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) { // REQUEST_CODE는 권한 요청 코드와 일치해야 합니다.
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) {
                // 모든 권한이 부여되었습니다. 필요한 작업을 계속할 수 있습니다.
                Log.d("Permissions", "All required permissions have been granted.")
            } else {
                // 필요한 권한 중 하나 이상이 거부되었습니다.
                Log.d("Permissions", "Required permissions are not granted.")
            }
        }
    }

    fun setBluetoothManagerListener(listener:BluetoothManager.Listener){
        bluetoothManager.listener=listener
    }

    @Deprecated("old")
    fun getBluetoothManagerOnActivity(): BluetoothManager_Old {
        return bluetoothManagerOld
    }
}
