package com.liquidskr.btclient

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
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
            .setCancelable(false)
            .create().show()
    }
    fun showAlertDialog(title: String, message: String, label:String, callback : DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(label, callback)
            .setCancelable(false)
            .create().show()
    }
    // setCancelable 빼고 저 콜백 DialogInterface.OnclickListener -> Unit -> Unit 뭐 이런걸로. + setOnDismissListener(callback)

    fun showAlertDialog(title: String, message: String, positiveCallback :DialogInterface.OnClickListener,negativeCallback:DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인",positiveCallback)
            .setNegativeButton("취소",negativeCallback)
            .setCancelable(false)
            .create().show()
    }
    fun showAlertDialog(title: String, message: String, positiveLabel : String, negativeLabel : String, positiveCallback :DialogInterface.OnClickListener,negativeCallback:DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveLabel,positiveCallback)
            .setNegativeButton(negativeLabel,negativeCallback)
            .setCancelable(false)
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
    fun showTextDialog(title: String, defaultText: String, inputType:Int , callback: (String)->Unit){
        val editText = EditText(activity)
        editText.setText(defaultText)
        editText.inputType = inputType

        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("확인"){_,_->
                callback(editText.text.toString())
            }
            .setNegativeButton("취소", null)
            .create().show()
    }

    fun showSingleChoiceDialog(title:String, items:Array<String>, callback:(Int)->Unit){
        var result : Int = 0
        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setPositiveButton("확인",){ _,_ ->
                callback(result)
            }
            .setNegativeButton("취소",null)
            .setSingleChoiceItems(items, 0) { dialog, which ->
                result = which
            }
            .create().show()
    }

}