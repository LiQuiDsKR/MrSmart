    package com.liquidskr.fragment

    import SharedViewModel
    import android.annotation.SuppressLint
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.LinearLayout
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.liquidskr.btclient.BluetoothManager
    import com.liquidskr.btclient.Constants
    import com.liquidskr.btclient.DialogUtils
    import com.liquidskr.btclient.DialogUtils.showTextDialog
    import com.liquidskr.btclient.MainActivity
    import com.liquidskr.btclient.BluetoothDeviceSaveService
    import com.liquidskr.btclient.R

    class SettingsFragment() : Fragment() {
        private lateinit var toolImportBtn: LinearLayout
        private lateinit var labelImportBtn: LinearLayout
        private lateinit var outstandingImportBtn: LinearLayout
        private lateinit var pcNameSetBtn: LinearLayout
        private lateinit var toolboxSetBtn: LinearLayout
        private lateinit var closeBtn: LinearLayout

        private lateinit var popupLayout: View

        private val bluetoothDeviceSaveService = BluetoothDeviceSaveService.getInstance()

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

            closeBtn.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            pcNameSetBtn.setOnClickListener {
                val currentPCName = bluetoothDeviceSaveService.getPCName()?:""
                val callback: (String) -> Unit = { text ->
                    bluetoothDeviceSaveService.insertPCName(text)
                }
                showTextDialog("정비실 노트북의 Mac 주소를 입력해주세요.",currentPCName,callback)
            }

            toolboxSetBtn.setOnClickListener {
                val type = Constants.BluetoothMessageType.TOOLBOX_ALL
                val data = ""
                (requireActivity() as MainActivity).bluetoothManager.send(type,data)
            }

            toolImportBtn.setOnClickListener {
                DialogUtils.showAlertDialog(
                    "안내",
                    "기준정보를 새로 불러오시겠습니까?\n기준정보를 모두 받는 데 2~5분 정도가 소요됩니다."
                    ,{ _,_->
                        //기준정보는 뭐 별 거 없지만 rentalRequestSheetForm같은 것들은 검수과정 필요
                        val type =Constants.BluetoothMessageType.MEMBERSHIP_ALL_COUNT
                        val data =""
                        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
                    }, { _,_->} )
            }
            labelImportBtn.setOnClickListener{
                DialogUtils.showAlertDialog(
                    "안내",
                    "선반 QR 코드 정보를 새로 불러오시겠습니까?\n정보를 모두 받는 데 2~5분 정도가 소요됩니다."
                    ,{ _,_->
                        val type = Constants.BluetoothMessageType.TOOLBOX_TOOL_LABEL_ALL_COUNT
                        val data = ""
                        (requireActivity() as MainActivity).bluetoothManager.send(type,data)
                    }, { _,_->} )
            }

            return view
        }
    }