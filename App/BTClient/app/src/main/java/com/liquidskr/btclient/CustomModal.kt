package com.liquidskr.btclient

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class CustomModal(private val context: Context, private val count: Int) {
    interface OnCountsConfirmedListener {
        fun onCountsConfirmed(counts: IntArray)
    }
    private var countsConfirmedListener: OnCountsConfirmedListener? = null

    lateinit var goodCount: TextView
    lateinit var faultCount: TextView
    lateinit var damageCount: TextView
    lateinit var lossCount: TextView
    lateinit var discardCount: TextView

    lateinit var incrementGood: FrameLayout
    lateinit var incrementFault: FrameLayout
    lateinit var incrementDamage: FrameLayout
    lateinit var incrementLoss: FrameLayout
    lateinit var incrementDiscard: FrameLayout

    lateinit var decrementGood: FrameLayout
    lateinit var decrementFault: FrameLayout
    lateinit var decrementDamage: FrameLayout
    lateinit var decrementLoss: FrameLayout
    lateinit var decrementDiscard: FrameLayout

    // 현재 각 상태의 수량을 저장하는 변수들
    private var goodCountVal = count
    private var faultCountVal = 0
    private var damageCountVal = 0
    private var lossCountVal = 0
    private var discardCountVal = 0

    private val handler = Handler(Looper.getMainLooper())

    private var counts = intArrayOf(goodCountVal, faultCountVal, damageCountVal, lossCountVal, discardCountVal)
    // 다이얼로그를 생성하고 보여주는 함수
    fun show() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.modal_layout, null)

        goodCount = view.findViewById(R.id.goodCount)
        faultCount = view.findViewById(R.id.faultCount)
        damageCount = view.findViewById(R.id.damageCount)
        lossCount = view.findViewById(R.id.lossCount)
        discardCount = view.findViewById(R.id.discardCount)

        incrementGood = view.findViewById(R.id.incrementGood)
        incrementFault = view.findViewById(R.id.incrementFault)
        incrementDamage = view.findViewById(R.id.incrementDamage)
        incrementLoss = view.findViewById(R.id.incrementLoss)
        incrementDiscard = view.findViewById(R.id.incrementDiscard)

        decrementGood = view.findViewById(R.id.decrementGood)
        decrementFault = view.findViewById(R.id.decrementFault)
        decrementDamage = view.findViewById(R.id.decrementDamage)
        decrementLoss = view.findViewById(R.id.decrementLoss)
        decrementDiscard = view.findViewById(R.id.decrementDiscard)

        // 초기값 설정
        goodCount.text = goodCountVal.toString()
        faultCount.text = faultCountVal.toString()
        damageCount.text = damageCountVal.toString()
        lossCount.text = lossCountVal.toString()
        discardCount.text = discardCountVal.toString()

        // 증가 및 감소 버튼에 대한 리스너 설정
        incrementGood.setOnClickListener { incrementCount(goodCount,0) }
        decrementGood.setOnClickListener { decrementCount(goodCount,0) }
        incrementFault.setOnClickListener { incrementCount(faultCount,1) }
        decrementFault.setOnClickListener { decrementCount(faultCount,1) }
        incrementDamage.setOnClickListener { incrementCount(damageCount,2) }
        decrementDamage.setOnClickListener { decrementCount(damageCount,2) }
        incrementLoss.setOnClickListener { incrementCount(lossCount,3) }
        decrementLoss.setOnClickListener { decrementCount(lossCount,3) }
        incrementDiscard.setOnClickListener { incrementCount(discardCount,4) }
        decrementDiscard.setOnClickListener { decrementCount(discardCount,4) }

        dialogBuilder.setView(view)
        dialogBuilder.setPositiveButton("확인") { _, _ ->
            // 확인 버튼을 눌렀을 때 수량과 각 상태의 합이 일치하는지 확인
            val totalSum = counts.sum()
            if (totalSum != count) {
                // 예외처리
                handler.post {
                    // Toast.makeText(activity, "수량이 맞지 않아 실패", Toast.LENGTH_SHORT).show()
                }
            } else {
                notifyCountsConfirmed()
            }
        }
        dialogBuilder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    fun setOnCountsConfirmedListener(listener: OnCountsConfirmedListener) {
        countsConfirmedListener = listener
    }

    private fun notifyCountsConfirmed() {
        countsConfirmedListener?.onCountsConfirmed(counts)
    }

    // 상태 증가 함수
    private fun incrementCount(textView: TextView, index: Int) {
        if (counts.sum() < count) {
            val newCnt = counts[index] + 1
            counts[index] = newCnt
            textView.text = (newCnt).toString()
        }
    }

    // 상태 감소 함수
    private fun decrementCount(textView: TextView, index: Int) {
        if (counts[index] > 0) {
            val newCnt = counts[index] - 1
            counts[index] = newCnt
            textView.text = (newCnt).toString()
        }
    }
}