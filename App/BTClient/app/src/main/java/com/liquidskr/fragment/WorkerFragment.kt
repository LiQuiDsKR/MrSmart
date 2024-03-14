package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.membership.Role

class WorkerFragment : Fragment() {
    private lateinit var loginBtn: Button
    private lateinit var idTextField: EditText

    private lateinit var popupLayout: View

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)

        idTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // 포커스가 주어졌을 때의 동작
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_enable_bg)
            } else {
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_bg)
            }
        }
        loginBtn.setOnClickListener {
            try {
                var code = idTextField.text.toString()
                val membershipService = MembershipService.getInstance()
                var member = membershipService.getMembershipByCode(code)
                if (member.role != Role.USER) {
                    DialogUtils.showAlertDialog("로그인 실패","해당 직원은 작업자가 아닙니다.")
                } else {
                    val fragment = WorkerLobbyFragment(member)
                    sharedViewModel.loginWorker = member
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack("WorkerLogin")
                        .commit()
                }
            } catch (e: IllegalStateException) {
                Log.d("login",e.toString())
                DialogUtils.showAlertDialog("로그인 실패", "사용자 정보가 없습니다.")
            } catch (e: Exception) {
                Log.d("login",e.toString())
                DialogUtils.showAlertDialog("로그인 실패", "알 수 없는 오류가 발생했습니다.")
            }
        }
        return view
    }
    companion object {
        fun newInstance(): WorkerFragment {
            return WorkerFragment()
        }
    }
    fun popBackStack() {
        val fragmentManager = childFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            // 백스택이 비어있으면 앱을 종료하거나 다른 작업을 수행할 수 있습니다.
        }
    }
}