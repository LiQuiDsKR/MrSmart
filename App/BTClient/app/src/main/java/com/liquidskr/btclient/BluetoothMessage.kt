package com.liquidskr.btclient

data class BluetoothMessage(
    val type: RequestType,
    val params: String,
    val callback: BluetoothManager.RequestCallback
)

