package com.liquidskr.btclient

import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.tool.TagDto

/**
 * tag, toolboxToolLabel 등 qrcode 입력을 감지해야 하는 Fragment들이 구현해야 함.
 */
interface InputHandler {
    /**
     * Activity가 키 입력 감지 시 호출하는 메서드.
     * 입력된 값을 파라미터로 받아서, BluetoothManager에게 송신하거나 할 수 있게끔 한다.
     */
    fun handleInput(input:String)

    /**
     * handlerInput에서 송신한 정보의 Response를 처리하는 곳.
     * 대체로 TagService가 handler.post로 호출한다.
     */
    fun handleResponse(response: Any)

    //fun handleResponse(response: OutstandingRentalSheetDto)
}