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
import androidx.fragment.app.FragmentManager
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.DialogUtils
import com.liquidskr.btclient.MainActivity
import com.liquidskr.btclient.R

class ReconnectFragment : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    var bluetoothManager : BluetoothManager? = null

    private val bluetoothManagerListener = object : BluetoothManager.Listener{
        override fun onDisconnected() {
            //do nothing
        }

        override fun onRequestStarted() {
            Log.d("progressbar","엥 여긴 왜 실행됩니까 여기는 실행될 리가 없어야 합니다")
        }

        override fun onRequestProcessed(context: String, processedAmount: Int, totalAmount: Int) {
            //TODO("Not yet implemented")
            Log.d("reconnecting", "$context : $processedAmount/$totalAmount")
            setProgressBar(processedAmount,totalAmount,context)
        }

        override fun onRequestEnded() {
            DialogUtils.showAlertDialog("연결됨", "블루투스 연결 성공"){_,_->close()}
        }

        override fun onRequestFailed(message: String) {
            DialogUtils.showAlertDialog(
                title = "재접속 실패",
                message = "서버와 접속에 실패했습니다. 다시 시도하시겠습니까?",
                positiveCallback = {_,_-> bluetoothManager?.connect() },
                negativeCallback = {_,_->
                    DialogUtils.showAlertDialog("종료","앱을 종료합니다. 블루투스 연결 상태를 다시 확인하고 실행해주세요.")
                    {_,_-> activity?.finish() }
                }
            )
        }

        override fun onException(message: String) {
            DialogUtils.showAlertDialog("재접속 실패", message){_,_->close()}
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reconnect, container, false)
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
        try{
            val activity = requireActivity()
            val fragmentManager : FragmentManager = activity.supportFragmentManager
            fragmentManager.popBackStack()
        }catch (e : Exception){
            Log.d("bluetooth",e.toString())
        }
    }


    override fun onResume() {
        super.onResume()
        bluetoothManager = (requireActivity() as MainActivity).bluetoothManager
    }
    override fun onPause() {
        super.onPause()
        bluetoothManager=null
    }
}