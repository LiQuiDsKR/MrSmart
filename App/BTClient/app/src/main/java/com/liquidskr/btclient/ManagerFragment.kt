package com.liquidskr.btclient

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
class ManagerFragment : Fragment() {
    lateinit var loginBtn: Button
    lateinit var loginTextField: EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager, container, false)

        // loginBtn을 레이아웃에서 찾아서 초기화
        loginBtn = view.findViewById(R.id.LoginBtn)
        loginTextField = view.findViewById(R.id.LoginTextField)


        loginBtn.setOnClickListener {
            var id = loginTextField.text.toString()
            var dbHelper = DatabaseHelper(requireContext())
            var name = dbHelper.getToolNameByCode(id)
            Toast.makeText(requireContext(),name + "님 반갑습니다!", Toast.LENGTH_SHORT).show()

        }


        return view
    }
    companion object {
        fun newInstance(): ManagerFragment {
            return ManagerFragment()
        }
    }
}