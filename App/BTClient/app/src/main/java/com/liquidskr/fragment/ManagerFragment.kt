package com.liquidskr.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipService
import com.mrsmart.standard.membership.Role
import java.lang.IllegalArgumentException

class ManagerFragment : Fragment(){
    private lateinit var loginBtn: Button
    private lateinit var idTextField: EditText
    private lateinit var pwTextField: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager, container, false)
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        pwTextField = view.findViewById(R.id.PWtextField)

        idTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_enable_bg)
            } else {
                idTextField.setBackgroundResource(R.drawable.edittext_rounded_bg)
            }
        }
        pwTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                pwTextField.setBackgroundResource(R.drawable.edittext_rounded_enable_bg)
            } else {
                pwTextField.setBackgroundResource(R.drawable.edittext_rounded_bg)
            }
        }

        loginBtn.setOnClickListener {
            try {
                val id = idTextField.text.toString()
                val pw = pwTextField.text.toString()
                if (MembershipService.getInstance().loginManager(id, pw)) {
                    val fragment = ManagerLobbyFragment()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack("ManagerLogin")
                        .commit()
                }
            } catch (e: IllegalArgumentException){
                Log.d("login",e.toString())
                DialogUtils.showAlertDialog("로그인 실패", "아이디 또는 비밀번호가 일치하지 않습니다.")
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
