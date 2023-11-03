package com.liquidskr.btclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class WorkerFragment : Fragment() {
    lateinit var loginBtn: ImageButton
    lateinit var idTextField: EditText

    lateinit var searchuserBtn: ImageButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker, container, false)

        // loginBtn을 레이아웃에서 찾아서 초기화
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        searchuserBtn = view.findViewById(R.id.SearchUserBtn)


        loginBtn.setOnClickListener {
            var id = idTextField.text.toString()
            var dbHelper = DatabaseHelper(requireContext())
            var name = dbHelper.getMembershipNameById(id)
                Toast.makeText(requireContext(), name + "님 환영합니다.", Toast.LENGTH_SHORT).show()
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
        fun newInstance(): WorkerFragment {
            return WorkerFragment()
        }
    }
}