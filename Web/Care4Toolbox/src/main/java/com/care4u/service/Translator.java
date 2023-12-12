package com.care4u.service;

import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;
import com.care4u.domain.Message;
import com.care4u.manager.BluetoothServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translator {
	
	private static final Logger logger = LoggerFactory.getLogger(BluetoothServer.class);
	int dataCheckStep = 0; // switch 를 위한 상태변수
	int dataSize = 0; // 보내는 데이터의 byte 크기
	String sendingMessage = "";
	
	public void onReceivedString(BluetoothCommunicationHandler handler, String receivedString) {
		if (sendingMessage.length() > 0) { //  not equal to blank
			dataSize = sendingMessage.getBytes().length;
			switch(dataCheckStep) {
			case 0:
				handler.sendData(Integer.toString(dataSize));
				dataCheckStep = 1;
				break;
			case 1:
				if (receivedString.equals(Integer.toString(dataSize))) {
					handler.sendData(sendingMessage);
					dataCheckStep = 2;
					break;
				} else {
					dataCheckStep = 0;
					break;
				}
				// break;
			case 2:
				if (receivedString.equals("DATASIZE_OK")) {
					dataCheckStep = 0; // 초기화
					dataSize = 0; // 초기화
				} else if (receivedString.equals("DATASIZE_OVERFLOW")) {
					dataCheckStep = 2; // 데이터 수신 중 오류발생, 내용이 제대로 전달되지 않았으므로 sendingMessage 재전송, 2단계로 설정
				} else if (receivedString.equals("DATASIZE_UNDERFLOW")) {
					dataCheckStep = 2; // 데이터 수신 중 오류발생, 내용이 제대로 전달되지 않았으므로 sendingMessage 재전송, 2단계로 설정
				} else {
					dataCheckStep = 2; // 데이터 수신 중 오류발생, 내용이 제대로 전달되지 않았으므로 sendingMessage 재전송, 2단계로 설정 **TIMEOUT**
				}
				break;
			default:
				logger.info("Invalid dataCheckStep");
				break;
			}
		}		
	}
	
	public void setSendingMessage(String sendingMessage) {
		this.sendingMessage = sendingMessage;
	}
}
