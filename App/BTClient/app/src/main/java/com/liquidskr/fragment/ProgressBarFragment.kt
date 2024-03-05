package com.liquidskr.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R

class ProgressBarFragment : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private val bluetoothManagerListener = object : BluetoothManager.Listener{
        override fun onDisconnected() {
            val reconnectFrag = ReconnectFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.popupLayout,reconnectFrag)
                .addToBackStack(null)
                .commit()
        }

        override fun onRequestStarted() {
            Log.d("progressbar","Progress started 사실 이게 계속 뜨는게 의도된게 아니긴 한데 분기 나누기 귀찮고 문제 없어서 그냥 냅둠")
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            //TODO("Not yet implemented")
            setProgressBar(processedAmount,totalAmount,context)
        }

        override fun onRequestEnded() {
            Log.d("progressbar","Request Ended Successfully")
            DialogUtils.showAlertDialog("성공","처리가 정상적으로 완료되었습니다."){
                _,_-> close()
            }
        }

        override fun onRequestFailed(message: String) {
            DialogUtils.showAlertDialog("통신 실패", message){_,_->close()}
        }

        override fun onException(message: String) {
            DialogUtils.showAlertDialog("통신 실패", message){_,_->close()}
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_progress_bar, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)

        (requireActivity() as MainActivity).setBluetoothManagerListener(bluetoothManagerListener)

        // Set up a touch listener that consumes all touch events
        view.setOnTouchListener { _, _ ->true}

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up a Back Button listener that consumes all Back Button events
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun setProgressBar(progress:Int,total:Int,context:String){
        progressBar.max=total
        progressBar.progress=progress

        val finalStr : String = "$context [$progress/$total]"
        progressText.text = finalStr
    }

    fun close(){
        requireActivity().supportFragmentManager.popBackStack()
    }
}