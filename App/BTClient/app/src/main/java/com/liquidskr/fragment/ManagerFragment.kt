package com.liquidskr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R

class ManagerFragment : Fragment() {
    lateinit var loginBtn: ImageButton
    lateinit var idTextField: EditText
    lateinit var pwTextField: EditText

    lateinit var searchuserBtn: ImageButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager, container, false)

        // loginBtn을 레이아웃에서 찾아서 초기화
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        pwTextField = view.findViewById(R.id.PWtextField)
        searchuserBtn = view.findViewById(R.id.SearchUserBtn)


        loginBtn.setOnClickListener {
            try {
                var code = idTextField.text.toString()
                var dbHelper = DatabaseHelper(requireContext())
                var password = dbHelper.getMembershipPasswordById(code)
                var member = dbHelper.getMembershipByCode(code)
                Toast.makeText(requireContext(), member.toString(), Toast.LENGTH_SHORT).show()
                if (pwTextField.text.toString().equals(password)) {
                    Toast.makeText(requireContext(),"로그인 성공", Toast.LENGTH_SHORT).show()
                }
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(requireContext(), "로그인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }


        }
        /*
        searchuserBtn.setOnClickListener {
            val fragment = WorkerFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }*/


        return view
    }
    companion object {
        fun newInstance(): ManagerFragment {
            return ManagerFragment()
        }
    }
}