package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalToolAdapter
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetFormDto
import com.mrsmart.standard.rental.RentalRequestToolFormDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import java.util.Locale

class WorkerRentalFragment() : Fragment() {
    lateinit var leaderSearchBtn: ImageButton
    lateinit var qrEditText: EditText
    lateinit var qrcodeBtn: ImageButton
    lateinit var addToolBtn: ImageButton
    lateinit var selectAllBtn: ImageButton
    lateinit var confirmBtn: ImageButton
    lateinit var clearBtn: ImageButton

    lateinit var workerName: TextView
    lateinit var leaderName: TextView
    private lateinit var recyclerView: RecyclerView

    var worker: MembershipSQLite? = null
    var leader: MembershipSQLite? = null

    var gson = Gson()

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker_rental, container, false)
        var dbHelper = DatabaseHelper(requireContext())

        leaderSearchBtn = view.findViewById(R.id.LeaderSearchBtn)
        qrEditText = view.findViewById((R.id.QR_EditText))
        qrcodeBtn = view.findViewById(R.id.QRcodeBtn)
        addToolBtn = view.findViewById(R.id.AddToolBtn)
        selectAllBtn = view.findViewById(R.id.SelectAllBtn)
        confirmBtn = view.findViewById(R.id.ConfirmBtn)
        clearBtn = view.findViewById(R.id.ClearBtn)

        workerName = view.findViewById(R.id.BorrowerName)
        leaderName = view.findViewById(R.id.LeaderName)

        recyclerView = view.findViewById(R.id.ManagerLobby_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        worker = sharedViewModel.loginWorker
        workerName.text = sharedViewModel.loginWorker.name
        leader = sharedViewModel.leader
        leaderName.text = sharedViewModel.leader.name

        Log.d("asdf", worker.toString())
        Log.d("asdf", sharedViewModel.worker.toString())

        leaderSearchBtn.setOnClickListener {
            val bundle = Bundle()
            val fragment = WorkerMembershipFindFragment.newInstance(2) // type = 2
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                //.addToBackStack(null)
                .commit()
        }
        qrcodeBtn.setOnClickListener {
            if (!qrEditText.isFocused) {
                qrEditText.requestFocus()

            }
        }
        qrEditText.setOnEditorActionListener { _, actionId, event ->
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // I want to make my keyboard must be type in English, not a Korean.
            qrEditText.textLocale = Locale.ENGLISH

            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val toolId = fixCode(qrEditText.text.toString().replace("\n", ""))
                try {
                    sharedViewModel.rentalRequestToolList.add(dbHelper.getToolByCode(toolId))
                    recyclerViewUpdate()
                } catch (e: UninitializedPropertyAccessException) {
                    Toast.makeText(requireContext(), "읽어들인 QR코드에 해당하는 공구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                qrEditText.text.clear()

                // Use a Handler to set focus after a delay
                Handler().postDelayed({
                    qrEditText.requestFocus()
                }, 100) // You can adjust the delay as needed

                return@setOnEditorActionListener true
            }
            false
        }

        addToolBtn.setOnClickListener {
            val fragment = ToolFindFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        selectAllBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalToolAdapter) {
                    adapter.selectAllTools(recyclerView, adapter)
                }
            }
        }
        confirmBtn.setOnClickListener {
            recyclerView.adapter?.let { adapter ->
                if (adapter is RentalToolAdapter) {
                    val rentalRequestToolFormDtoList: MutableList<RentalRequestToolFormDto> = mutableListOf()
                    for (tool: ToolDtoSQLite in adapter.selectedToolsToRental) {
                        val holder = recyclerView.findViewHolderForAdapterPosition(adapter.tools.indexOf(tool)) as? RentalToolAdapter.RentalToolViewHolder
                        val toolCount = holder?.toolCount?.text?.toString()?.toIntOrNull() ?: 0
                        rentalRequestToolFormDtoList.add(RentalRequestToolFormDto(tool.id, toolCount))
                    }
                    // Log.d("test",  gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, 5222,rentalRequestToolFormDtoList.toList())))
                    if (!(worker!!.code.equals(""))) {
                        if (!(leader!!.code.equals(""))) {
                            val rentalRequestSheet = gson.toJson(RentalRequestSheetFormDto("DefaultWorkName", worker!!.id, leader!!.id, 5222 ,rentalRequestToolFormDtoList.toList()))
                            sharedViewModel.worker = MembershipSQLite(0,"","","","","","","", "" )
                            sharedViewModel.leader = MembershipSQLite(0,"","","","","","","", "" )
                            requireActivity().supportFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "리더를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "작업자를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        clearBtn.setOnClickListener {
            sharedViewModel.rentalRequestToolList.clear()
            val adapter = RentalToolAdapter(sharedViewModel.rentalRequestToolList)
            recyclerView.adapter = adapter
        }
        recyclerViewUpdate()
        return view
    }
    fun fixCode(input: String): String {
        val typoMap = mapOf(
            'ㅁ' to 'A',
            'ㅠ' to 'B',
            'ㅊ' to 'C',
            'ㅇ' to 'D',
            'ㄷ' to 'E',
            'ㄹ' to 'F',
            'ㅎ' to 'G'
        )

        val correctedText = StringBuilder()

        for (char in input) {
            val correctedChar = typoMap[char] ?: char
            correctedText.append(correctedChar)
        }

        return correctedText.toString()
    }
    fun recyclerViewUpdate() {
        val adapter = RentalToolAdapter(sharedViewModel.rentalRequestToolList)
        recyclerView.adapter = adapter
    }
    companion object {
        fun newInstance(): WorkerRentalFragment {
            val fragment = WorkerRentalFragment()
            return fragment
        }
    }
}