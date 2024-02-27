package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.BluetoothManager_Old
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R


class LobbyFragment : Fragment() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var bluetoothBtn: ImageButton
    lateinit var settingBtn: ImageButton
    lateinit var bluetoothManagerOld: BluetoothManager_Old
    private var isPopupVisible = false

    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    var backPressedTime=0L


    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }

    private val bluetoothManagerListener = object : BluetoothManager.Listener{
        override fun onDisconnected() {
            //TODO("Not yet implemented")
        }

        override fun onReconnected() {
            //TODO("Not yet implemented")
        }

        override fun onRequestStarted() {
            //TODO("Not yet implemented")
            val progressBarFrag = ProgressBarFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.bluetoothPopupLayout,progressBarFrag)
                .addToBackStack(null)
                .commit()
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            //TODO("Not yet implemented")
        }

        override fun onRequestEnded() {
            //TODO("Not yet implemented")
        }

        override fun onRequestFailed(message: String) {
            //TODO("Not yet implemented")
        }

        override fun onException(message: String) {
            //TODO("Not yet implemented")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby, container, false)

        popupLayout = view.findViewById(R.id.bluetoothPopupLayout)

        workerBtn = view.findViewById(R.id.workerBtn)
        managerBtn = view.findViewById(R.id.managerBtn)
        bluetoothBtn = view.findViewById(R.id.bluetoothBtn)
        settingBtn = view.findViewById(R.id.SettingBtn)


        (requireActivity() as MainActivity).setBluetoothManagerListener(bluetoothManagerListener)

        workerBtn.setOnClickListener {
            showPopup()
            val fragment = WorkerFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        managerBtn.setOnClickListener {
            showPopup()
            val fragment = ManagerFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        settingBtn.setOnClickListener {
            val fragment = SettingsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로 가기 버튼 콜백 등록
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 현재 시간과 마지막으로 뒤로 가기 버튼이 눌린 시간을 비교
                if (backPressedTime + Constants.BACK_BUTTON_DOUBLE_PRESS_CHECK_INTERVAL > System.currentTimeMillis()) {
                    // 2초 이내에 뒤로 가기 버튼을 다시 누르면 앱 종료
                    requireActivity().finish()
                } else {
                    // 첫 번째로 뒤로 가기 버튼을 눌렀을 때 토스트 메시지 표시
                    Toast.makeText(requireContext(), "종료하려면 한 번 더 탭하세요", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
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