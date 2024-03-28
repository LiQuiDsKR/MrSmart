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
            Log.d("progressbar","Progress started")
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            //TODO("Not yet implemented")
            setProgressBar(processedAmount,totalAmount,context)
        }

        override fun onRequestEnded(message: String) {
            Log.d("progressbar","Request Ended Successfully")
            if (message.isNotEmpty()){
                DialogUtils.showAlertDialog("성공",message){
                        _,_-> close()
                }
            }else{
                close()
            }
        }

        override fun onRequestFailed(message: String) {
            DialogUtils.showAlertDialog("통신 실패", message){_,_->close()}
        }

        override fun onException(message: String) {
            DialogUtils.showAlertDialog("오류",message){_,_->close()}
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_progress_bar, container, false)
        popupLayout = view.findViewById(R.id.popupLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)

        (requireActivity() as MainActivity).registerBluetoothManagerListener(bluetoothManagerListener)

        // Set up a touch listener that consumes all touch events
        view.setOnTouchListener { _, _ ->true}

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("progressbar","Progress Bar Fragment Created")

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

    override fun onDetach() {
        super.onDetach()
        (requireActivity() as MainActivity).unregisterBluetoothManagerListener()
        Log.d("progressbar","Progress Bar Fragment Detached")
    }
    fun close(){
        requireActivity().supportFragmentManager.popBackStack()
    }
}