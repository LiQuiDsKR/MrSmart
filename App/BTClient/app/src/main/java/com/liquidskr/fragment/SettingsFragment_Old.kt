    package com.liquidskr.fragment

    import SharedViewModel
    import android.annotation.SuppressLint
    import android.app.AlertDialog
    import android.content.Context
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.EditText
    import android.widget.LinearLayout
    import android.widget.ProgressBar
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.google.gson.Gson
    import com.liquidskr.btclient.BluetoothManager_Old
    import com.liquidskr.btclient.Constants
    import com.liquidskr.btclient.DatabaseHelper
    import com.liquidskr.btclient.MainActivity
    import com.liquidskr.btclient.R
    import com.liquidskr.listener.MembershipRequest
    import com.liquidskr.listener.OutstandingRequest
    import com.liquidskr.listener.TagRequest
    import com.liquidskr.listener.ToolBoxToolLabelRequest
    import com.liquidskr.listener.ToolRequest
    import com.mrsmart.standard.page.Page
    import com.mrsmart.standard.tool.ToolboxCompressDto
    import java.lang.reflect.Type

    class SettingsFragment_Old() : Fragment() {
        lateinit var importStandard: LinearLayout
        lateinit var importQRData: LinearLayout
        lateinit var importOutstanding: LinearLayout
        lateinit var setServerPCName: LinearLayout
        lateinit var setToolBox: LinearLayout
        lateinit var closeBtn: LinearLayout
        private val mainActivity: MainActivity
            get() = requireActivity() as MainActivity
        private lateinit var bluetoothManagerOld: BluetoothManager_Old

        private lateinit var popupLayout: View
        private lateinit var progressBar: ProgressBar
        private lateinit var progressText: TextView
        private var isPopupVisible = false

        private val handler = Handler(Looper.getMainLooper())
        private val gson = Gson()
        private val dbHelper = DatabaseHelper.getInstance()

        private lateinit var membershipRequest: MembershipRequest
        private lateinit var toolRequest: ToolRequest
        private lateinit var toolBoxToolLabelRequest: ToolBoxToolLabelRequest
        private lateinit var tagRequest: TagRequest
        private lateinit var outstandingRequest: OutstandingRequest

        private val membershipRequestListener = object: MembershipRequest.Listener {
            override fun onNextPage(pageNum: Int) {
                requestMembership(pageNum)
            }

            override fun onLastPageArrived() {
                importTool(dbHelper)
            }

            override fun onError(e: Exception) {
                // 연결 끊고 모달 띄우고 재접속
            }
        }
        private val toolRequestListener = object: ToolRequest.Listener {
            override fun onNextPage(pageNum: Int) {
                requestTool(pageNum)
            }

            override fun onLastPageArrived() {
                handler.post {
                    hidePopup()
                    Toast.makeText(requireActivity(), "기준 정보를 정상적으로 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    showAlertModal("기준정보 수신 완료","기준 정보를 정상적으로 불러왔습니다.")
                }
            }

            override fun onError(e: Exception) {
            }

        }
        private val toolBoxToolLabelRequestListener = object: ToolBoxToolLabelRequest.Listener {
            override fun onNextPage(pageNum: Int) {
                requestToolboxToolLabel(pageNum)
            }

            override fun onLastPageArrived() {
                handler.post {
                    hidePopup()
                    Toast.makeText(requireActivity(), "선반 QR코드 정보를 정상적으로 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    showAlertModal("선반 QR코드 정보 수신 완료","선반 QR코드 정보를 정상적으로 불러왔습니다.")
                }
            }

            override fun onError(e: Exception) {
            }

        }
        private val tagRequestListener = object: TagRequest.Listener {
            override fun onNextPage(pageNum: Int) {
                requestTag(pageNum)
            }

            override fun onLastPageArrived() {

            }

            override fun onError(e: Exception) {
            }

        }
        private val outstandingRequestListener = object: OutstandingRequest.Listener {
            override fun onNextPage(pageNum: Int) {
                requestOutstanding(pageNum)
            }

            override fun onLastPageArrived() {
                handler.post {
                    hidePopup()
                    Toast.makeText(requireActivity(), "반납전표 정보를 정상적으로 불러왔습니다.", Toast.LENGTH_SHORT).show()
                    showAlertModal("반납전표 정보 수신 완료","반납전표 정보를 정상적으로 불러왔습니다.")
                }
            }

            override fun onError(e: Exception) {
            }

        }

        private val alertModalListener = object : MainActivity.AlertModalListener {
            override fun onConfirmButtonClicked() {

            }

            override fun onCancelButtonClicked() {

            }
        }
        private val importStandardModalListener = object : MainActivity.AlertModalListener {
            override fun onConfirmButtonClicked() {
                showPopup() // progressBar appear
                progressText.text = ""
                importMembership(dbHelper)
            }

            override fun onCancelButtonClicked() {

            }
        }
        private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        }
        private fun showAlertModal(title: String, content: String) {
            mainActivity.showAlertModal(title, content, alertModalListener)
        }
        private fun showImportStandardModal(title: String, content: String) {
            mainActivity.showAlertModal(title, content, importStandardModalListener)
        }
        @SuppressLint("MissingInflatedId")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_settings, container, false)
            popupLayout = view.findViewById(R.id.popupLayout)
            progressBar = view.findViewById(R.id.progressBar)
            progressText = view.findViewById(R.id.progressText)
            importStandard = view.findViewById(R.id.importStandard)
            importQRData = view.findViewById(R.id.importQRData)
            importOutstanding = view.findViewById(R.id.importOutstanding)
            setServerPCName = view.findViewById(R.id.setServerPCName)
            setToolBox = view.findViewById(R.id.setToolBox)
            closeBtn = view.findViewById(R.id.closeBtn)
            popupLayout.setOnTouchListener { _, _ -> true }
            bluetoothManagerOld = (requireActivity() as MainActivity).getBluetoothManagerOnActivity()

            closeBtn.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            setServerPCName.setOnClickListener {
                var currentPCName = bluetoothManagerOld.pcName
                showTextDialog(requireContext(), "정비실 노트북(PC)의 이름을 입력하세요.", currentPCName) { string ->
                    bluetoothManagerOld.pcName = string
                    dbHelper.refreshDeviceData(string)
                }
            }

            setToolBox.setOnClickListener {

                showPopup()
                try {
                    bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TOOLBOX_ALL,"",object: BluetoothManager_Old.RequestCallback {
                        override fun onSuccess(result: String, type: Type) {
                            try {
                                var toolboxList: List<ToolboxCompressDto> = gson.fromJson(result, type)
                                sharedViewModel.toolBoxList = toolboxList
                                handler.post{
                                    hidePopup()
                                }

                                var toolboxNameList = mutableListOf<String>()
                                for (toolboxDto in sharedViewModel.toolBoxList) {
                                    toolboxNameList.add(toolboxDto.name)
                                }
                                var toolboxNameArray = toolboxNameList.toTypedArray()

                                var selectedToolbox = "선강정비1실"

                                for (toolboxDto in sharedViewModel.toolBoxList) {
                                    if (toolboxDto.id == sharedViewModel.toolBoxId) {
                                        selectedToolbox = toolboxDto.name
                                    }
                                }
                                handler.post{
                                    showSelectionDialog(requireContext(), "정비실을 선택하세요.", toolboxNameArray, selectedToolbox) {string ->
                                        for (toolboxDto in sharedViewModel.toolBoxList) {
                                            if (string == toolboxDto.name) {
                                                sharedViewModel.toolBoxId = toolboxDto.id
                                            }
                                        }
                                        dbHelper.refreshToolboxData(sharedViewModel.toolBoxId, string)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("importToolbox", e.toString())
                            }
                        }
                        override fun onError(e: Exception) {
                            hidePopup() // progressBar hide
                            handler.post {
                                Toast.makeText(requireActivity(), "정비실 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                } catch (e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "블루투스에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            importStandard.setOnClickListener {
                showImportStandardModal("안내", "기준정보를 새로 불러오시겠습니까?\n기준정보를 모두 받는 데 5~6 분 정도가 소요됩니다.")
            }
            importQRData.setOnClickListener{
                showPopup() // progressBar appear
                importTBT(dbHelper)
            }
            importOutstanding.setOnClickListener{
                showPopup() // progressBar appear
                importOutstanding(dbHelper)
            }

            return view
        }

        private fun importMembership(dbHelper: DatabaseHelper) {
            var membershipCnt = 0
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.MEMBERSHIP_ALL_COUNT,"",object: BluetoothManager_Old.RequestCallback {
                override fun onSuccess(result: String, type: Type) {
                    try {
                        membershipCnt = result.toInt()
                        val totalPage = Math.ceil(membershipCnt / 10.0).toInt()
                        progressBar.max = totalPage
                        dbHelper.clearMembershipTable()
                        membershipRequest = MembershipRequest(totalPage, membershipCnt, dbHelper, membershipRequestListener)

                        requestMembership(0)
                    } catch (e: Exception) {
                        Log.d("importMembership", e.toString())
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "사원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        private fun importTool(dbHelper: DatabaseHelper) {
            var toolCnt = 0
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TOOL_ALL_COUNT,"",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    try {
                        toolCnt = result.toInt()
                        val totalPage = Math.ceil(toolCnt / 10.0).toInt()
                        progressBar.max = totalPage
                        dbHelper.clearToolTable()
                        toolRequest = ToolRequest(totalPage, toolCnt, dbHelper, toolRequestListener)

                        requestTool(0)
                    } catch (e: Exception) {
                        Log.d("importTool", e.toString())
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "공기구 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        private fun importTBT(dbHelper: DatabaseHelper) {
            var toolboxToolLabelCnt = 0
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TOOLBOX_TOOL_LABEL_ALL_COUNT,"{toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager_Old.RequestCallback {
                override fun onSuccess(result: String, type: Type) {
                    try {
                        toolboxToolLabelCnt = result.toInt()
                        val totalPage = Math.ceil(toolboxToolLabelCnt / 10.0).toInt()
                        progressBar.max = totalPage
                        dbHelper.clearTBTTable()
                        toolBoxToolLabelRequest = ToolBoxToolLabelRequest(totalPage, toolboxToolLabelCnt, dbHelper, toolBoxToolLabelRequestListener)

                        requestToolboxToolLabel(0)
                    } catch (e: Exception) {
                        Log.d("importToolboxToolLabel", e.toString())
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "선반 코드 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        private fun importTag(dbHelper: DatabaseHelper) {
            var tagCnt = 0
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TAG_ALL_COUNT,"{toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager_Old.RequestCallback {
                override fun onSuccess(result: String, type: Type) {
                    try {
                        tagCnt = result.toInt()
                        val totalPage = Math.ceil(tagCnt / 10.0).toInt()
                        progressBar.max = totalPage
                        dbHelper.clearTagTable()
                        tagRequest = TagRequest(totalPage, tagCnt, dbHelper, tagRequestListener)

                        requestTag(0)
                    } catch (e: Exception) {
                        Log.d("importTag", e.toString())
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "태그 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        private fun importOutstanding(dbHelper: DatabaseHelper) {
            var outstandingCnt = 0
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT,"{}",object: BluetoothManager_Old.RequestCallback {
                override fun onSuccess(result: String, type: Type) {
                    try {
                        outstandingCnt = result.toInt()
                        val totalPage = Math.ceil(outstandingCnt / 10.0).toInt()
                        progressBar.max = totalPage
                        dbHelper.clearOutstandingTable()
                        outstandingRequest = OutstandingRequest(totalPage, outstandingCnt, dbHelper, outstandingRequestListener)

                        requestOutstanding(0)
                    } catch (e: Exception) {
                        Log.d("importOutstanding", e.toString())
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "반납전표 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        fun requestMembership(pageNum: Int) {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.MEMBERSHIP_ALL,"{\"size\":${10},\"page\":${pageNum}}",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    var page: Page = gson.fromJson(result, type)
                    membershipRequest.process(page)
                    requireActivity().runOnUiThread {
                        progressBar.progress = page.pageable.page
                        if (page.total / 10 > 0) {
                            progressText.text = "사원 정보 다운로드 중, ${page.pageable.page}/${page.total / 10}, ${page.pageable.page * 100 / (page.total / 10)}%"
                        }
                    }
                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "사원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        fun requestTool(pageNum: Int) {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TOOL_ALL,"{\"size\":${10},\"page\":${pageNum}}",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    var page: Page = gson.fromJson(result, type)
                    toolRequest.process(page) //
                    requireActivity().runOnUiThread {
                        progressBar.progress = page.pageable.page
                        if (page.total / 10 > 0) {
                            progressText.text = "공기구 정보 다운로드 중, ${page.pageable.page}/${page.total / 10}, ${page.pageable.page * 100 / (page.total / 10)}%"
                        }
                    }

                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "공기구 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        fun requestToolboxToolLabel(pageNum: Int) {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TOOLBOX_TOOL_LABEL_ALL,"{\"size\":${10},\"page\":${pageNum},toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    var page: Page = gson.fromJson(result, type)
                    toolBoxToolLabelRequest.process(page)
                    requireActivity().runOnUiThread {
                        progressBar.progress = page.pageable.page
                        if (page.total / 10 > 0) {
                            progressText.text = "선반 코드 정보 다운로드 중, ${page.pageable.page}/${page.total / 10}, ${page.pageable.page * 100 / (page.total / 10)}%"
                        }
                    }

                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "선반 코드 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        fun requestTag(pageNum: Int) {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.TAG_ALL,"{\"size\":${10},\"page\":${pageNum},toolboxId:${sharedViewModel.toolBoxId}}",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    var page: Page = gson.fromJson(result, type)
                    tagRequest.process(page)
                    requireActivity().runOnUiThread {
                        progressBar.progress = page.pageable.page
                        if (page.total / 10 > 0) {
                            progressText.text = "태그 정보 다운로드 중, ${page.pageable.page}/${page.total / 10}, ${page.pageable.page * 100 / (page.total / 10)}%"
                        }
                    }

                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "태그 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        fun requestOutstanding(pageNum: Int) {
            bluetoothManagerOld.requestData(Constants.BluetoothMessageType.OUTSTANDING_RENTAL_SHEET_PAGE_ALL,"{\"size\":${10},\"page\":${pageNum}}",object: BluetoothManager_Old.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    var page: Page = gson.fromJson(result, type)
                    outstandingRequest.process(page)
                    requireActivity().runOnUiThread {
                        progressBar.progress = page.pageable.page
                        if (page.total / 10 > 0) {
                            progressText.text = "반납전표 정보 다운로드 중, ${page.pageable.page}/${page.total / 10}, ${page.pageable.page * 100 / (page.total / 10)}%"
                        }
                    }

                }
                override fun onError(e: Exception) {
                    hidePopup() // progressBar hide
                    handler.post {
                        Toast.makeText(requireActivity(), "반납전표 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
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
        fun showTextDialog(context: Context, title: String, defaultText: String, callback: (String) -> Unit) {
            val editText = EditText(context)
            editText.setText(defaultText)

            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("OK") { _, _ ->
                    val enteredText = editText.text.toString()
                    callback.invoke(enteredText)
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }
        fun showSelectionDialog(context: Context, title: String, items: Array<String>, defaultSelectedItem: String?, callback: (String) -> Unit) {
            val selectedIndex = items.indexOf(defaultSelectedItem)

            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(items, selectedIndex) { _, which ->
                    // 사용자가 선택한 항목의 인덱스를 얻어옴
                    val selectedItem = items[which]
                    callback.invoke(selectedItem)
                }
                .setPositiveButton("OK") { dialog, _ ->
                    // OK 버튼을 누를 때의 동작
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Cancel 버튼을 누를 때의 동작
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }

        override fun onDestroyView() {
            super.onDestroyView()
        }
    }