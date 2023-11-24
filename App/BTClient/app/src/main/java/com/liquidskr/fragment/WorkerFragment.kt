package com.liquidskr.fragment

import SharedViewModel
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
import androidx.lifecycle.ViewModelProvider
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.R

class WorkerFragment : Fragment() {
    private lateinit var loginBtn: ImageButton
    private lateinit var idTextField: EditText

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_worker, container, false)

        // loginBtn을 레이아웃에서 찾아서 초기화
        loginBtn = view.findViewById(R.id.LoginBtn)
        idTextField = view.findViewById(R.id.IDtextField)
        try {
            loginBtn.setOnClickListener {
                var code = idTextField.text.toString()
                var dbHelper = DatabaseHelper(requireContext())
                var member = dbHelper.getMembershipByCode(code)
                val fragment = WorkerLobbyFragment(member.toMembership())
                sharedViewModel.loginWorker = member
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(requireContext(), "로그인할 수 없습니다.", Toast.LENGTH_SHORT).show()
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