package com.care4u.communication.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.care4u.domain.Message;
import com.care4u.service.Translator;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public class BluetoothCommunicationHandler {

	private static final Logger logger = LoggerFactory.getLogger(BluetoothCommunicationHandler.class);

	private String macaddress;
	
	Translator translator;
	
	private final BluetoothConnectHandler.Listener connectorHandlerListener =  new BluetoothConnectHandler.Listener() {
		@Override
        public void onException(String description) {
            // TODO Auto-generated method stub
			if (listener != null) listener.onException(BluetoothCommunicationHandler.this, description);            
        }
        @Override
        public void onDataArrived(byte[] datas) {
            // TODO Auto-generated method stub        	
        	logger.debug("arrived message : " + new String(datas));
        	if (listener != null) listener.onDataArrived(BluetoothCommunicationHandler.this, datas.length, new String(datas));
        	
        }
        @Override
        public void onConnected() {
            // TODO Auto-generated method stub
        	if (listener != null) listener.onConnected(BluetoothCommunicationHandler.this);
        	translator = new Translator();
        }
        @Override
        public void onDisconnected(){
        	if (listener != null) listener.onDisconnected(BluetoothCommunicationHandler.this);
        }
	};
	
	private BluetoothConnectHandler connectorHandler;
			
	public interface Listener{
		public void onDataArrived(BluetoothCommunicationHandler handler, int size, String data);
		public void onConnected(BluetoothCommunicationHandler handler);
		public void onDataSent(BluetoothCommunicationHandler handler, int size, String data);
		public void onException(BluetoothCommunicationHandler handler, String message);
		public void onDisconnected(BluetoothCommunicationHandler handler);
		
		//public void onRequestedMembershipAll(BluetoothCommunicationHandler handler);
	}
	private Listener listener;
	
	public BluetoothCommunicationHandler(String macaddress, StreamConnection streamConnection, BluetoothCommunicationHandler.Listener listener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
		
		this.macaddress = macaddress;
		
		connectorHandler = new BluetoothConnectHandler(streamConnection, connectorHandlerListener);
	}
	
	public void sendData(String message){
		logger.info("send message : " + message);
	    byte[] data = message.getBytes();
	    ByteBuffer buffer = ByteBuffer.allocate(4); // int는 4바이트
	    buffer.putInt(data.length);
	    byte[] sizeByte = buffer.array();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    try {
	    	outputStream.write(sizeByte);
	        outputStream.write(data);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    byte[] finalData = outputStream.toByteArray();
	    connectorHandler.sendData(finalData);
	}
	
	public void destroy(){
		logger.debug("destroy...");
		if (connectorHandler != null) connectorHandler.disconnect();
		connectorHandler = null;
	}

	public String getMacaddress() {
		return macaddress;
	}
}