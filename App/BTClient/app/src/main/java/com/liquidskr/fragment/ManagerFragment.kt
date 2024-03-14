package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.InputProcessor
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.membership.Role

class ManagerFragment : Fragment(){
    lateinit var loginBtn: Button
    lateinit var idTextField: EditText
    lateinit var pwTextField: EditText

    private lateinit var popupLayout: View


    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        pwTextField = view.findViewById(R.id.PWtextField)

        idTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // 포커스가 주어졌을 때의 동작
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_enable_bg)
            } else {
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_bg)
            }
        }
        pwTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // 포커스가 주어졌을 때의 동작
                pwTextField.setBackgroundResource(R.drawable.edittext_rounded_enable_bg)
            } else {
                pwTextField.setBackgroundResource(R.drawable.edittext_rounded_bg)
            }
        }

        loginBtn.setOnClickListener {
            try {
                var code = idTextField.text.toString()
                var membershipService = MembershipService.getInstance()
                var member = membershipService.getMembershipByCode(code)
                var password = member.password
                if (member.role != Role.MANAGER) {
                    DialogUtils.showAlertDialog("로그인 실패","해당 직원은 관리자가 아닙니다.")
                }else if (pwTextField.text.toString() != password) {
                    DialogUtils.showAlertDialog("로그인 실패", "비밀번호가 맞지 않습니다.")
                } else {
                    sharedViewModel.loginManager = member
                    val fragment = ManagerLobbyFragment(member)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack("ManagerLogin")
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
        fun newInstance(): ManagerFragment {
            return ManagerFragment()
        }
    }
}
