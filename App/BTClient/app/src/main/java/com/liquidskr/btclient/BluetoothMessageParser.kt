package com.liquidskr.btclient

import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BluetoothMessageParser (
    private val listener: BluetoothMessageParser.Listener
) {
    interface Listener {
        fun onDataArrived(datas: ByteArray)
        fun onException(type: Constants.ExceptionType, description: String)
    }

    private val byteArrayOutputStream = ByteArrayOutputStream()
    private var mSizeDataHolder: DataHolder = DataHolder(ByteArray(Constants.INTEGER_BYTE_SIZE))
    private var mBodyDataHolder: DataHolder = DataHolder()
    private var mDataIndex: Int = 0
    private var mSizeReadFlag: Boolean = false //SizeDataHolder가 값을 다 읽었는지 여부
    private var mBodyReadFlag: Boolean = false //BodyDataHolder가 값을 다 읽었는지 여부

    fun initialize() {
        byteArrayOutputStream.reset()
        mBodyDataHolder.reset()
        mDataIndex = 0
        mSizeDataHolder.index = 0
        mBodyDataHolder.index = 0
        mSizeReadFlag = false
        mBodyReadFlag = false
    }

    fun process(data: ByteArray): ByteArray {
        try {
            Log.d("bluetooth", "decode Str : ${String(data, Charsets.UTF_8)}}")

            byteArrayOutputStream.write(data)
            byteArrayOutputStream.flush()

            val receivedData = byteArrayOutputStream.toByteArray()

            if (!readSizeInfo(receivedData)) return ByteArray(0) //
            if (!readBodyInfo(receivedData)) return ByteArray(0)

            listener.onDataArrived(mBodyDataHolder.data)

            if (mDataIndex < receivedData.size) {
                val nextData = ByteArray(data.size - mDataIndex)
                System.arraycopy(data, mDataIndex, nextData, 0, nextData.size)
                Log.d("bluetooth", "start next datas...")
                initialize()
                return nextData
            }
        } catch (e: Exception) {
            listener.onException(Constants.ExceptionType.BLUETOOTH_DEFAULT_EXCEPTION, e.toString())
        }
        initialize()
        return ByteArray(0)
    }

    private fun readSizeInfo(data: ByteArray): Boolean {
        //이미 다 읽었음
        if (mSizeReadFlag) return true

        //읽어봤는데 아직 다 안 들어옴
        if (!readData(data, mSizeDataHolder)) return false

        //읽어봤는데 다 들어옴
        mSizeReadFlag = true
        val length = byte2int(mSizeDataHolder.data, 0)
        Log.d("bluetooth", "readSize=${length}, data index=${mDataIndex}")

        mBodyDataHolder.reset(length)
        return true
    }

    private fun readBodyInfo(data: ByteArray): Boolean {
        //이미 다 읽었음
        if (mBodyReadFlag) return true

        //읽어봤는데 아직 다 안 들어옴
        if (!readData(data, mBodyDataHolder)) return false

        //읽어봤는데 다 들어옴
        mBodyReadFlag = true
        Log.d("bluetooth", "readBody=${mBodyDataHolder.data}, data index=$mDataIndex")
        return true
    }

    private fun readData(srcData: ByteArray, destDataHolder: DataHolder): Boolean {
        val copyLength = (srcData.size - mDataIndex).coerceAtMost(destDataHolder.data.size)
        if (copyLength <= 0) return false

        System.arraycopy(srcData, mDataIndex, destDataHolder.data, destDataHolder.index, copyLength)
        destDataHolder.index += copyLength
        mDataIndex += copyLength
        return destDataHolder.index == destDataHolder.data.size
    }

    fun byte2int(buffer: ByteArray, startIndex: Int): Int {
        //Big_endian체크 필요없으면 이거 안써도 됨
        val byteBuffer = ByteBuffer.wrap(buffer, startIndex, Constants.INTEGER_BYTE_SIZE)
            .order(ByteOrder.BIG_ENDIAN)
        return byteBuffer.int
    }

    private class DataHolder(var data: ByteArray = ByteArray(0)) {
        var index = 0

        fun reset(size: Int = 0) {
            index = 0
            data = ByteArray(size)
        }
    }
}
