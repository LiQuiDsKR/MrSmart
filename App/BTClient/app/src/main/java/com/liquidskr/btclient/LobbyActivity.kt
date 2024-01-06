package com.liquidskr.btclient

import SharedViewModel
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
    lateinit var testBtn: Button
    lateinit var testEdit: EditText
    lateinit var bluetoothManager: BluetoothManager
    lateinit var managerRentalFragment: ManagerRentalFragment
    lateinit var managerReturnFragment: ManagerReturnFragment
    lateinit var workerSelfRentalFragment: WorkerSelfRentalFragment
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

    private var listener: MyScannerListener.Listener? = null

    fun setListener(listener: MyScannerListener.Listener?) {
        this.listener = listener
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val handler = Handler(Looper.getMainLooper())
        /*
        handler.post {
            Toast.makeText(this, event.keyCode, Toast.LENGTH_SHORT).show()
        }*/
        if (event.action == KeyEvent.ACTION_DOWN) {
            Log.d("SCANNER", "some key pressed, $event")
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // listener?.onTextFinished()
                return super.dispatchKeyEvent(event)
            } else {
                when (event.keyCode) {
                    KEYCODE_0 -> sharedViewModel.qrScannerText += "0"
                    KEYCODE_1 -> sharedViewModel.qrScannerText += "1"
                    KEYCODE_2 -> sharedViewModel.qrScannerText += "2"
                    KEYCODE_3 -> sharedViewModel.qrScannerText += "3"
                    KEYCODE_4 -> sharedViewModel.qrScannerText += "4"
                    KEYCODE_5 -> sharedViewModel.qrScannerText += "5"
                    KEYCODE_6 -> sharedViewModel.qrScannerText += "6"
                    KEYCODE_7 -> sharedViewModel.qrScannerText += "7"
                    KEYCODE_8 -> sharedViewModel.qrScannerText += "8"
                    KEYCODE_9 -> sharedViewModel.qrScannerText += "9"
                }
                Log.d("SCANNER", "shared : ${sharedViewModel.qrScannerText}")

                /*
                handler.post {
                    Toast.makeText(this, event.keyCode,Toast.LENGTH_SHORT).show()
                }*/
                return super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val gson = Gson()
        val context = this

        val handler = Handler(Looper.getMainLooper())

        bluetoothManager = BluetoothManager(this, this)
        managerRentalFragment = ManagerRentalFragment()
        // managerReturnFragment = ManagerReturnFragment()
        workerSelfRentalFragment = WorkerSelfRentalFragment()

        popupLayout = findViewById(R.id.bluetoothPopupLayout)
        progressBar = findViewById(R.id.bluetoothProgressBar)
        progressText = findViewById(R.id.bluetoothProgressText)

        testBtn = findViewById(R.id.testBtn)
        testEdit = findViewById(R.id.testEdit)
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
        testBtn.setOnClickListener {
            bluetoothManager.requestData(RequestType.TEST, "{string:\"${testEdit.text}\"}", object:
                BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    if (result == "good") {
                        handler.post{
                            Toast.makeText(context, "rec : ${result}",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handler.post{
                            Toast.makeText(context, "not good, rec : ${result}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onError(e: Exception) {
                    e.printStackTrace()
                    handler.post{
                        Toast.makeText(context, "ERROR",Toast.LENGTH_SHORT).show()
                    }
                }
            })
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
