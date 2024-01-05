package com.liquidskr.btclient

interface ScannerListener {
    fun onTextChanged(text: String)
    fun onTextFinished()
}

class MyScannerListener(private val callback: (String) -> Unit) : ScannerListener {
    override fun onTextChanged(text: String) {
        callback.invoke(text)
    }

    override fun onTextFinished() {
        // Handle text finished event
        callback.invoke("") // Empty string or provide necessary data
    }
}