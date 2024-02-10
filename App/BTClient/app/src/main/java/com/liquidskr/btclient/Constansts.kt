package com.liquidskr.btclient

object Constansts {
    enum class BluetoothExceptionType{
        DEFAULT_EXCEPTION,
        PERMISSION_MISSING,
        NO_PAIRED_DEVICE,
        CONNECTION_FAILED,
        DATABASE_HELPER_NOT_INITIALIZED,
        SQLITE_EXCEPTION,
        EXTERNAL_DATABASE_PERMISSION_MISSING,
        DISCONNECTED,
    }
}