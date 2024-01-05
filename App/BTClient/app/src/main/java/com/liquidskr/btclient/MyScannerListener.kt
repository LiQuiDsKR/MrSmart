package com.liquidskr.btclient

class MyScannerListener() {
    interface Listener {
        fun onTextFinished()
    }
    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun onEnter() {
        listener?.onTextFinished()
    }

}