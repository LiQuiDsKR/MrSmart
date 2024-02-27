package com.liquidskr.btclient

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText

object DialogUtils {

    lateinit var activity:MainActivity

    fun initialize(activity :MainActivity){
        this.activity = activity
    }

    fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인",null)
            .create().show()
    }
    fun showAlertDialog(title: String, message: String, callback : DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", callback)
            .create().show()
    }
    fun showAlertDialog(title: String, message: String, label:String, callback : DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(label, callback)
            .create().show()
    }
    fun showAlertDialog(title: String, message: String, positiveCallback :DialogInterface.OnClickListener,negativeCallback:DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인",positiveCallback)
            .setNegativeButton("취소",negativeCallback)
            .create().show()
    }

    fun showTextDialog(title: String, defaultText: String, callback: (String)->Unit){
        val editText = EditText(activity)
        editText.setText(defaultText)

        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("확인"){_,_->
                callback(editText.text.toString())
            }
            .setNegativeButton("취소", null)
            .create().show()
    }

}