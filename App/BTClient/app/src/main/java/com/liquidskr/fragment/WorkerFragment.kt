package com.liquidskr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R

class WorkerFragment : Fragment() {
    private lateinit var loginBtn: ImageButton
    private lateinit var idTextField: EditText

    private lateinit var searchuserBtn: ImageButton
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker, container, false)

        // loginBtn을 레이아웃에서 찾아서 초기화
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        searchuserBtn = view.findViewById(R.id.SearchUserBtn)


        loginBtn.setOnClickListener {
            val id = idTextField.text.toString()
            val dbHelper = DatabaseHelper(requireContext())
            val name = dbHelper.getMembershipNameById(id)
                Toast.makeText(requireContext(), name + "님 환영합니다.", Toast.LENGTH_SHORT).show()
            }

        searchuserBtn.setOnClickListener {
            val fragment = UserListFragment.newInstance()
            val transaction = childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
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