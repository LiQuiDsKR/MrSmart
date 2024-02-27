package com.liquidskr.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
            //TODO("Not yet implemented")
        }

        override fun onReconnected() {
            //TODO("Not yet implemented")
        }

        override fun onRequestStarted() {
            Log.d("progressbar","엥 여긴 왜 실행됩니까 여기는 실행될 리가 없어야 합니다")
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            //TODO("Not yet implemented")
            setProgressBar(processedAmount,totalAmount,context)
        }

        override fun onRequestEnded() {
            close()
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