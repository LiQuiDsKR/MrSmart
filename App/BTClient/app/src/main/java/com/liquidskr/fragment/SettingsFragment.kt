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
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RequestType
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import com.mrsmart.standard.tool.ToolboxToolLabelDto
import java.lang.reflect.Type

class SettingsFragment() : Fragment() {
    lateinit var importStandard: LinearLayout
    lateinit var setServerPCName: LinearLayout
    lateinit var setToolBox: LinearLayout
    lateinit var closeBtn: LinearLayout

    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    private var isPopupVisible = false
    private val progressRunnable = object : Runnable {
        override fun run() {
            // 여기에 반복해서 실행할 코드 추가
            if (popupLayout.visibility == 0) { // Visible
                val currentBytes = bluetoothManager.currentBytes
                val totalBytes = bluetoothManager.totalBytes
                try {
                    progressText.text = "${currentBytes}/${totalBytes}, ${currentBytes * 100 / totalBytes}%"
                } catch (e:Exception) {

                }
                progressBar.progress = currentBytes
                progressBar.max = totalBytes
            } else if (popupLayout.visibility == 8) { // Gone

            }
            // 0.5초 후에 다시 실행
            handler.postDelayed(this, 200)
        }
    }

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        importStandard = view.findViewById(R.id.importStandard)
        setServerPCName = view.findViewById(R.id.setServerPCName)
        setToolBox = view.findViewById(R.id.setToolBox)
        closeBtn = view.findViewById(R.id.closeBtn)
        popupLayout.setOnTouchListener { _, _ -> true }
        bluetoothManager = (requireActivity() as LobbyActivity).getBluetoothManagerOnActivity()

        val dbHelper = DatabaseHelper(requireContext())
        closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        if (isPopupVisible) {
            disableBackButton()
        }
        setServerPCName.setOnClickListener {
            var currentPCName = bluetoothManager.pcName
            showTextDialog(requireContext(), "정비실 노트북(PC)의 이름을 입력하세요.", currentPCName) { string ->
                bluetoothManager.pcName = string
            }
        }
        var toolboxArray = arrayOf("선강정비1실", "선강정비2실", "선강정비3실", "선강정비4실", "선강정비5실")
        setToolBox.setOnClickListener {
            var selectedToolbox = "선강정비1실"
            if (sharedViewModel.toolBoxId.toInt() == 5222) {
                selectedToolbox = "선강정비1실"
            } else if (sharedViewModel.toolBoxId.toInt() == 5223) {
                selectedToolbox = "선강정비2실"
            } else if (sharedViewModel.toolBoxId.toInt() == 5224) {
                selectedToolbox = "선강정비3실"
            } else if (sharedViewModel.toolBoxId.toInt() == 5225) {
                selectedToolbox = "선강정비4실"
            } else if (sharedViewModel.toolBoxId.toInt() == 5226) {
                selectedToolbox = "선강정비5실"
            }
            showSelectionDialog(requireContext(), "정비실을 선택하세요.", toolboxArray, selectedToolbox) {string ->
                if (string == toolboxArray[0]) { // 선강정비1실
                    sharedViewModel.toolBoxId = 5222
                } else if (string == toolboxArray[1]) { // 선강정비2실
                    sharedViewModel.toolBoxId = 5223
                } else if (string == toolboxArray[2]) { // 선강정비3실
                    sharedViewModel.toolBoxId = 5224
                } else if (string == toolboxArray[3]) { // 선강정비4실
                    sharedViewModel.toolBoxId = 5225
                } else if (string == toolboxArray[4]) { // 선강정비5실
                    sharedViewModel.toolBoxId = 5226
                }
            }
        }

        importStandard.setOnClickListener {
            showPopup() // progressBar appear
            handler.postDelayed(progressRunnable, 200)
            progressText.text = ""
            bluetoothManager.requestData(RequestType.MEMBERSHIP_ALL,"",object: BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    dbHelper.clearMembershipTable()
                    var membershipList: List<Membership> = gson.fromJson(result, type)
                    for (member in membershipList) {
                        val id = member.id
                        val code = member.code
                        val password = member.password
                        val name = member.name
                        val part = member.partDto.name
                        val subPart = member.partDto.subPartDto.name
                        val mainPart = member.partDto.subPartDto.mainPartDto.name
                        val role = member.role.toString()
                        val employmentStatus = member.employmentStatus.toString()
                        dbHelper.insertMembershipData(id, code, password, name, part, subPart, mainPart, role, employmentStatus)
                        Log.d("Debug_Standard", MembershipSQLite(id, code, password, name, part, subPart, mainPart, role, employmentStatus).toString())
                    }
                    bluetoothManager.requestData(RequestType.TOOL_ALL,"",object: BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
                            dbHelper.clearToolTable()
                            var toolList: List<ToolDto> = gson.fromJson(result, type)
                            for (tool in toolList) {
                                val id = tool.id
                                val mainGroup = tool.subGroupDto.mainGroupDto.name
                                val subGroup = tool.subGroupDto.name
                                val code = tool.code
                                val krName = tool.name
                                val engName = tool.engName
                                val spec = tool.spec
                                val unit = tool.unit
                                val price = tool.price
                                val replacementCycle = tool.replacementCycle
                                val buyCode = ""
                                dbHelper.insertToolData(id, mainGroup, subGroup, code, krName, engName, spec, unit, price, replacementCycle, buyCode)
                                Log.d("Debug_Standard", ToolDtoSQLite(id, mainGroup, subGroup, code, krName, engName, spec, unit, price, replacementCycle, buyCode).toString())
                            }
                            bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL_ALL,"{\"toolboxId\":${sharedViewModel.toolBoxId}}",object:
                                BluetoothManager.RequestCallback{
                                override fun onSuccess(result: String, type: Type) {
                                    dbHelper.clearTBTTable()
                                    var tbtList: List<ToolboxToolLabelDto> = gson.fromJson(result, type)
                                    for (tbt in tbtList) {
                                        val id = tbt.id
                                        val toolbox = tbt.toolboxDto.id
                                        val location = tbt.location
                                        val tool = tbt.toolDto.id
                                        val qrcode = tbt.qrcode

                                        dbHelper.insertTBTData(id, toolbox, location, tool, qrcode)
                                    }
                                    bluetoothManager.requestData(RequestType.TAG_ALL,"{\"toolboxId\":${sharedViewModel.toolBoxId}}",object:
                                        BluetoothManager.RequestCallback{
                                        override fun onSuccess(result: String, type: Type) {
                                            dbHelper.clearTagTable()
                                            var tagList: List<TagDto> = gson.fromJson(result, type)
                                            for (tag in tagList) {
                                                val id = tag.id
                                                val macaddress = tag.macaddress
                                                val tool = tag.toolDto.id
                                                val taggroup = tag.tagGroup

                                                dbHelper.insertTagData(id, macaddress, tool, taggroup)
                                            }
                                            dbHelper.close()
                                            Log.d("popup","asdf")
                                            handler.removeCallbacks(progressRunnable) // progressBar callback remove
                                            requireActivity().runOnUiThread {
                                                hidePopup() // progressBar hide
                                                Toast.makeText(activity, "기준정보를 정상적으로 불러왔습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onError(e: Exception) {
                                            e.printStackTrace()
                                        }
                                    })
                                }

                                override fun onError(e: Exception) {
                                    e.printStackTrace()
                                }
                            })
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
        }

        return view
    }
    private fun disableBackButton() {
    }

    private fun enableBackButton() {
        // 원래 동작으로 복원
    }
    private fun showPopup() {
        isPopupVisible = true
        disableBackButton()
        // Show the popup layout
        popupLayout.visibility = View.VISIBLE


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

    fun showSelectionDialog(
        context: Context,
        title: String,
        items: Array<String>,
        defaultSelectedItem: String?,
        callback: (String) -> Unit
    ) {
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

    private fun hidePopup() {
        isPopupVisible = false
        enableBackButton()
        // Hide the popup layout
        popupLayout.visibility = View.GONE

    }
    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트가 종료될 때 핸들러의 작업 중지
        handler.removeCallbacks(progressRunnable)
    }
}