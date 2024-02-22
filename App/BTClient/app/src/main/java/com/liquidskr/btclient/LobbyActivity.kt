package com.liquidskr.btclient

import PermissionManager
import SharedViewModel
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.fragment.ManagerFragment
import com.liquidskr.fragment.SettingsFragment
import com.liquidskr.fragment.WorkerFragment


class LobbyActivity : AppCompatActivity() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var bluetoothBtn: ImageButton
    lateinit var settingBtn: ImageButton
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothSocket: BluetoothSocket
    private var workerFragment: WorkerFragment? = null
    private var isPopupVisible = false

    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView


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
        setContentView(R.layout.activity_lobby)
        val gson = Gson()
        val context = this

        val dbHelper = DatabaseHelper.initInstance(this)
        val permissionManager = PermissionManager
        permissionManager.initialize(this)

        sharedViewModel.toolBoxId = dbHelper.getToolboxName()

        val handler = Handler(Looper.getMainLooper())

        bluetoothManager = BluetoothManager(this, this)
        popupLayout = findViewById(R.id.bluetoothPopupLayout)

        workerBtn = findViewById(R.id.workerBtn)
        managerBtn = findViewById(R.id.managerBtn)
        bluetoothBtn = findViewById(R.id.bluetoothBtn)
        settingBtn = findViewById(R.id.SettingBtn)

        //bluetoothManager.bluetoothOpen()

        workerBtn.setOnClickListener {
            showPopup()
            val fragment = WorkerFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        managerBtn.setOnClickListener {
            showPopup()
            val fragment = ManagerFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        bluetoothBtn.setOnClickListener {
            bluetoothManager.bluetoothOpen()
            bluetoothManager = getBluetoothManagerOnActivity()
        }

        settingBtn.setOnClickListener {
            val fragment = SettingsFragment(context)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun showAlertModal(title: String, content: String, listener: AlertModalListener) {
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
        if (requestCode == PermissionManager.REQUEST_CODE) { // REQUEST_CODE는 권한 요청 코드와 일치해야 합니다.
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

    fun getBluetoothManagerOnActivity(): BluetoothManager {
        return bluetoothManager
    }
    private fun showPopup() {
        isPopupVisible = true
        // Show the popup layout
        popupLayout.visibility = View.VISIBLE
    }
    private fun hidePopup() {
        isPopupVisible = false
        // Hide the popup layout
        popupLayout.visibility = View.GONE
    }
}
