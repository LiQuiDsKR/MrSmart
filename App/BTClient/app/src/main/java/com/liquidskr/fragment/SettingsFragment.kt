    package com.liquidskr.fragment

    import SharedViewModel
    import android.annotation.SuppressLint
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.LinearLayout
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.liquidskr.btclient.BluetoothManager
    import com.liquidskr.btclient.Constants
    import com.liquidskr.btclient.DialogUtils.createAlertDialog
    import com.liquidskr.btclient.DialogUtils.createTextDialog
    import com.liquidskr.btclient.MainActivity
    import com.liquidskr.btclient.PCNameService
    import com.liquidskr.btclient.R

    class SettingsFragment() : Fragment() {
        private lateinit var toolImportBtn: LinearLayout
        private lateinit var labelImportBtn: LinearLayout
        private lateinit var outstandingImportBtn: LinearLayout
        private lateinit var pcNameSetBtn: LinearLayout
        private lateinit var toolboxSetBtn: LinearLayout
        private lateinit var closeBtn: LinearLayout

        private lateinit var popupLayout: View
        private lateinit var progressBar: ProgressBar
        private lateinit var progressText: TextView
        private var isPopupVisible = false

        private val pcNameService = PCNameService(object : PCNameService.Listener{
            override fun onException(type: Constants.ExceptionType, description: String) {
                TODO("Not yet implemented")
            }
            override fun onInserted(size: Int, index: Int, total: Int) {
                TODO("Not yet implemented")
            }
        })

        private val bluetoothManagerListener = object : BluetoothManager.Listener{
            override fun onDisconnected() {
                TODO("Not yet implemented")
            }

            override fun onReconnected() {
                TODO("Not yet implemented")
            }

            override fun onRequestStarted() {
                TODO("Not yet implemented")
            }

            override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
                TODO("Not yet implemented")
            }

            override fun onRequestEnded() {
                TODO("Not yet implemented")
            }

            override fun onRequestFailed(message: String) {
                TODO("Not yet implemented")
            }

            override fun onException(message: String) {
                TODO("Not yet implemented")
            }
        }


        private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        }



        @SuppressLint("MissingInflatedId")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_settings, container, false)
            popupLayout = view.findViewById(R.id.popupLayout)
            toolImportBtn = view.findViewById(R.id.importStandard)
            labelImportBtn = view.findViewById(R.id.importQRData)
            outstandingImportBtn = view.findViewById(R.id.importOutstanding)
            pcNameSetBtn = view.findViewById(R.id.setServerPCName)
            toolboxSetBtn = view.findViewById(R.id.setToolBox)
            closeBtn = view.findViewById(R.id.closeBtn)

            (requireActivity() as MainActivity).setBluetoothManagerListener(bluetoothManagerListener)

            closeBtn.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            pcNameSetBtn.setOnClickListener {
                val currentPCName = pcNameService.getPCName()?:""
                val callback: (String) -> Unit = { text ->
                    pcNameService.insertPCName(text)
                }
                createTextDialog(requireContext(),"정비실 노트북(PC)의 이름을 입력하세요.",currentPCName,callback).show()
            }

            toolboxSetBtn.setOnClickListener {
                TODO("야호")
            }

            toolImportBtn.setOnClickListener {
                createAlertDialog(
                    requireContext(),
                    "안내",
                    "기준정보를 새로 불러오시겠습니까?\n기준정보를 모두 받는 데 5~6 분 정도가 소요됩니다.",
                ){ _,_->

                }

            }
            labelImportBtn.setOnClickListener{
            }
            outstandingImportBtn.setOnClickListener{
            }

            return view
        }

        override fun onResume() {
            super.onResume()
            requireActivity().
        }

        override fun onPause() {
            super.onPause()
        }

    }