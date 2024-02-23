package com.liquidskr.btclient

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText

object DialogUtils {
    fun createAlertDialog(context:Context, title: String, message: String): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인",null)
        return builder.create()
    }
    fun createAlertDialog(context: Context, title: String, message: String, callback : DialogInterface.OnClickListener): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", callback)
        return builder.create()
    }
    fun createAlertDialog(context: Context, title: String, message: String, label:String, callback : DialogInterface.OnClickListener): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(label, callback)
        return builder.create()
    }
    fun createAlertDialog(context: Context, title: String, message: String, positiveCallback :DialogInterface.OnClickListener,negativeCallback:DialogInterface.OnClickListener): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인",positiveCallback)
            .setNegativeButton("취소",negativeCallback)
        return builder.create()
    }

    fun createTextDialog(context: Context, title: String, defaultText: String, callback: (String)->Unit) :AlertDialog{
        val editText = EditText(context)
        editText.setText(defaultText)

        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("확인"){_,_->
                callback(editText.text.toString())
            }
            .setNegativeButton("취소", null)
        return builder.create()
    }

}