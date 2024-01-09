package com.liquidskr.btclient

import SharedViewModel
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_0
import android.view.KeyEvent.KEYCODE_1
import android.view.KeyEvent.KEYCODE_2
import android.view.KeyEvent.KEYCODE_3
import android.view.KeyEvent.KEYCODE_4
import android.view.KeyEvent.KEYCODE_5
import android.view.KeyEvent.KEYCODE_6
import android.view.KeyEvent.KEYCODE_7
import android.view.KeyEvent.KEYCODE_8
import android.view.KeyEvent.KEYCODE_9
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.KeyEventDispatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.liquidskr.fragment.ManagerFragment
import com.liquidskr.fragment.ManagerRentalFragment
import com.liquidskr.fragment.ManagerReturnFragment
import com.liquidskr.fragment.SettingsFragment
import com.liquidskr.fragment.ToolRegisterFragment
import com.liquidskr.fragment.WorkerFragment
import com.liquidskr.fragment.WorkerSelfRentalFragment
import com.mrsmart.standard.membership.MembershipSQLite
import java.lang.reflect.Type

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

        val dbHelper = DatabaseHelper(this)

        sharedViewModel.toolBoxId = dbHelper.getToolboxName()

        val handler = Handler(Looper.getMainLooper())

        bluetoothManager = BluetoothManager(this, this)
        popupLayout = findViewById(R.id.bluetoothPopupLayout)
        progressBar = findViewById(R.id.bluetoothProgressBar)
        progressText = findViewById(R.id.bluetoothProgressText)

        workerBtn = findViewById(R.id.workerBtn)
        managerBtn = findViewById(R.id.managerBtn)
        bluetoothBtn = findViewById(R.id.bluetoothBtn)
        settingBtn = findViewById(R.id.SettingBtn)

        bluetoothManager.bluetoothOpen()

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

    fun showCustomModal(title: String, content: String, listener: CustomModalListener) {
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
