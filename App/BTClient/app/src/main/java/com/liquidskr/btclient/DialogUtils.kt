package com.liquidskr.btclient

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputFilter
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

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
        if (inputType == android.text.InputType.TYPE_CLASS_NUMBER){
            editText.setRawInputType(android.text.InputType.TYPE_CLASS_NUMBER)
            val maxLength = 9
            val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            editText.filters = filters
        }

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
            .setPositiveButton("확인"){ _, _ ->
                callback(result)
            }
            .setNegativeButton("취소",null)
            .setSingleChoiceItems(items, 0) { dialog, which ->
                result = which
            }
            .create().show()
    }

    fun showReturnFormCountSelectDialog(
        count:Int,
        goodCountVal:Int,
        faultCountVal:Int,
        damageCountVal:Int,
        lossCountVal:Int,
        callback: (Int,Int,Int,Int,String)->Unit)
    {
        var goodCountVar = goodCountVal
        var faultCountVal = faultCountVal
        var damageCountVal = damageCountVal
        var lossCountVal = lossCountVal
        var sumCount = {goodCountVar + faultCountVal + damageCountVal + lossCountVal}

        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.modal_layout, null)

        var goodCount : TextView = view.findViewById(R.id.goodCount)
        var faultCount : TextView = view.findViewById(R.id.faultCount)
        var damageCount : TextView = view.findViewById(R.id.damageCount)
        var lossCount : TextView = view.findViewById(R.id.lossCount)

        var incrementGood : ImageView = view.findViewById(R.id.incrementGood)
        var incrementFault : ImageView = view.findViewById(R.id.incrementFault)
        var incrementDamage : ImageView= view.findViewById(R.id.incrementDamage)
        var incrementLoss : ImageView = view.findViewById(R.id.incrementLoss)
        incrementGood.setOnClickListener{if (sumCount()<count) goodCountVar++; goodCount.text = goodCountVar.toString()}
        incrementFault.setOnClickListener{if (sumCount()<count) faultCountVal++; faultCount.text = faultCountVal.toString()}
        incrementDamage.setOnClickListener{if (sumCount()<count) damageCountVal++; damageCount.text = damageCountVal.toString()}
        incrementLoss.setOnClickListener{if (sumCount()<count) lossCountVal++; lossCount.text = lossCountVal.toString()}

        var decrementGood : ImageView= view.findViewById(R.id.decrementGood)
        var decrementFault : ImageView = view.findViewById(R.id.decrementFault)
        var decrementDamage : ImageView = view.findViewById(R.id.decrementDamage)
        var decrementLoss : ImageView = view.findViewById(R.id.decrementLoss)
        decrementGood.setOnClickListener{if (goodCountVar>0) goodCountVar--; goodCount.text = goodCountVar.toString()}
        decrementFault.setOnClickListener{if (faultCountVal>0) faultCountVal--; faultCount.text = faultCountVal.toString()}
        decrementDamage.setOnClickListener{if (damageCountVal>0) damageCountVal--; damageCount.text = damageCountVal.toString()}
        decrementLoss.setOnClickListener{if (lossCountVal>0) lossCountVal--; lossCount.text = lossCountVal.toString()}

        var commentEdit : TextView = view.findViewById(R.id.commentEdit)

        // 초기값 설정
        goodCount.text = goodCountVar.toString()
        faultCount.text = faultCountVal.toString()
        damageCount.text = damageCountVal.toString()
        lossCount.text = lossCountVal.toString()

        builder.setView(view)
        builder.setPositiveButton("확인"){_,_->
            callback(
                goodCountVar,
                faultCountVal,
                damageCountVal,
                lossCountVal,
                commentEdit.text.toString()
            )
        }
        builder.create().show()
    }

}