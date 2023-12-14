package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        idTextField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                idTextField.requestFocus()
            }
        }
        loginBtn.setOnClickListener {
            try {
                var code = idTextField.text.toString()
                var dbHelper = DatabaseHelper(requireContext())
                var member = dbHelper.getMembershipByCode(code)
                if (member.role == "USER") {
                    val fragment = WorkerLobbyFragment(member.toMembership())
                    sharedViewModel.loginWorker = member
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "해당 직원은 작업자가 아닙니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(requireContext(), "로그인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    companion object {
        fun newInstance(): WorkerFragment {
            return WorkerFragment()
        }
    }
    fun showSoftKeyboard(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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