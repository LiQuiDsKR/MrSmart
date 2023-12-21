package com.liquidskr.btclient

import SharedViewModel
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.liquidskr.fragment.ManagerFragment
import com.liquidskr.fragment.ManagerRentalFragment
import com.liquidskr.fragment.ManagerReturnFragment
import com.liquidskr.fragment.SettingsFragment
import com.liquidskr.fragment.WorkerFragment
import com.liquidskr.fragment.WorkerRentalFragment
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.TagDto
import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import com.mrsmart.standard.tool.ToolboxToolLabelDto
import java.lang.reflect.Type

class LobbyActivity  : AppCompatActivity() {
    lateinit var workerBtn: ImageButton
    lateinit var managerBtn: ImageButton
    lateinit var dbSyncBtn: ImageButton
    //lateinit var testSendBtn: ImageButton
    lateinit var bluetoothBtn: ImageButton
    lateinit var settingBtn: ImageButton
    lateinit var bluetoothManager: BluetoothManager
    lateinit var managerRentalFragment: ManagerRentalFragment
    lateinit var managerReturnFragment: ManagerReturnFragment
    lateinit var workerRentalFragment: WorkerRentalFragment
    private var workerFragment: WorkerFragment? = null
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        val gson = Gson()

        val context = this
        bluetoothManager = BluetoothManager(this, this)
        managerRentalFragment = ManagerRentalFragment()
        managerReturnFragment = ManagerReturnFragment()
        workerRentalFragment = WorkerRentalFragment()

        workerBtn = findViewById(R.id.workerBtn)
        managerBtn = findViewById(R.id.managerBtn)
        dbSyncBtn = findViewById(R.id.DBSyncBtn)
        //testSendBtn = findViewById(R.id.testSendBtn)
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
        dbSyncBtn.setOnClickListener {
            bluetoothManager.requestData(RequestType.MEMBERSHIP_ALL,"",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    val dbHelper = DatabaseHelper(context)
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
                    bluetoothManager.requestData(RequestType.TOOL_ALL,"",object:BluetoothManager.RequestCallback{
                        override fun onSuccess(result: String, type: Type) {
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
                            dbHelper.clearTBTTable()
                            bluetoothManager.requestData(RequestType.TOOLBOX_TOOL_LABEL_ALL,"{\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
                                override fun onSuccess(result: String, type: Type) {
                                    var tbtList: List<ToolboxToolLabelDto> = gson.fromJson(result, type)
                                    for (tbt in tbtList) {
                                        val id = tbt.id
                                        val toolbox = tbt.toolboxDto.id
                                        val location = tbt.location
                                        val tool = tbt.toolDto.id
                                        val qrcode = tbt.qrcode

                                        dbHelper.insertTBTData(id, toolbox, location, tool, qrcode)
                                    }
                                    dbHelper.clearTagTable()
                                    bluetoothManager.requestData(RequestType.TAG_ALL,"{\"toolboxId\":${sharedViewModel.toolBoxId}}",object:BluetoothManager.RequestCallback{
                                        override fun onSuccess(result: String, type: Type) {
                                            var tagList: List<TagDto> = gson.fromJson(result, type)
                                            for (tag in tagList) {
                                                val id = tag.id
                                                val macaddress = tag.macaddress
                                                val tool = tag.toolDto.id
                                                val taggroup = tag.tagGroup

                                                dbHelper.insertTagData(id, macaddress, tool, taggroup)
                                            }
                                            dbHelper.close()
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
        /*
        testSendBtn.setOnClickListener {
            bluetoothManager.requestData(RequestType.TOOL_ALL,"",object:BluetoothManager.RequestCallback{
                override fun onSuccess(result: String, type: Type) {
                    val dbHelper = DatabaseHelper(context)
                    val ToolListType = object : TypeToken<List<ToolDto>>(){}.type
                    var toolList: List<ToolDto> = gson.fromJson(result, ToolListType)
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
                    dbHelper.close()
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            })
        }*/

        bluetoothBtn.setOnClickListener {
            bluetoothManager.bluetoothOpen()
        }
        settingBtn.setOnClickListener {
            val fragment = SettingsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
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
}
