package com.care4u.communication.bluetooth;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

public class BluetoothCommunicationHandler {

	private static final Logger logger = LoggerFactory.getLogger(BluetoothCommunicationHandler.class);

	private String macaddress;
	
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
        }
        @Override
        public void onConnected() {
            // TODO Auto-generated method stub
        	if (listener != null) listener.onConnected(BluetoothCommunicationHandler.this);
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
	}
	private Listener listener;
	
	public BluetoothCommunicationHandler(String macaddress, StreamConnection streamConnection, BluetoothCommunicationHandler.Listener listener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
		
		this.macaddress = macaddress;
		
		connectorHandler = new BluetoothConnectHandler(streamConnection, connectorHandlerListener);
	}
	
	public void sendData(String message){
		logger.debug("send message : " + message);
		connectorHandler.sendData(message.getBytes());
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