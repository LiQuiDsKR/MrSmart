package com.care4u.communication.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothCommunicationHandler {

	private static final Logger logger = LoggerFactory.getLogger(BluetoothCommunicationHandler.class);

	private String macaddress;
	private boolean startedOK;
	
	private Translator translator;
	private Translator.Listener traslatorListener = new Translator.Listener() {
		
		@Override
		public void onException(Exception ex) {
			// TODO Auto-generated method stub
			connectorHandler.disconnect();
			if (listener != null) listener.onException(BluetoothCommunicationHandler.this, ex.toString());
		}
		
		@Override
		public void onDataArrived(byte[] datas) {
			// TODO Auto-generated method stub
			if (listener != null) listener.onDataArrived(BluetoothCommunicationHandler.this, new String(datas));
		}
	};
	
	private final BluetoothConnectHandler.Listener connectorHandlerListener =  new BluetoothConnectHandler.Listener() {
		@Override
        public void onException(String description) {
            // TODO Auto-generated method stub
			if (listener != null) listener.onException(BluetoothCommunicationHandler.this, description);            
        }
        @Override
        public void onDataArrived(byte[] datas) {
            // TODO Auto-generated method stub
        	while(datas != null) {
        		datas = translator.process(datas);
        	}
        }
        @Override
        public void onConnected() {
            // TODO Auto-generated method stub
        	startedOK = true;
        	if (listener != null) listener.onConnected(BluetoothCommunicationHandler.this);
        }
        @Override
        public void onDisconnected(){
        	if (listener != null) listener.onDisconnected(BluetoothCommunicationHandler.this);
        }
	};
	
	private BluetoothConnectHandler connectorHandler;
			
	public interface Listener{
		public void onDataArrived(BluetoothCommunicationHandler handler, String data);
		public void onConnected(BluetoothCommunicationHandler handler);
		public void onDataSent(BluetoothCommunicationHandler handler, String data);
		public void onException(BluetoothCommunicationHandler handler, String message);
		public void onDisconnected(BluetoothCommunicationHandler handler);
		
		//public void onRequestedMembershipAll(BluetoothCommunicationHandler handler);
	}
	private Listener listener;
	
	public BluetoothCommunicationHandler(String macaddress, StreamConnection streamConnection, BluetoothCommunicationHandler.Listener listener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
		
		this.macaddress = macaddress;
		
		translator = new Translator(traslatorListener);
		
		connectorHandler = new BluetoothConnectHandler(streamConnection, connectorHandlerListener);
	}
	
	public void sendData(String message){
		logger.info("send message : " + message);
	    byte[] data = message.getBytes(StandardCharsets.UTF_8);
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
	
	public void disconnect(){
		logger.debug("disconnect...");
		if (connectorHandler != null) {
			connectorHandler.disconnect();
		}
		connectorHandler = null;
	}

	public String getMacaddress() {
		return macaddress;
	}
	
	public boolean isValid() {
		if (startedOK && connectorHandler.isConnected()) {
			return true;
		}
		return false;
	}

}