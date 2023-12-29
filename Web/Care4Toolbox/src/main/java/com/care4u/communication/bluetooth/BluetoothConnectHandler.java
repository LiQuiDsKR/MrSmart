package com.care4u.communication.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wimpi.modbus.util.ModbusUtil;

public class BluetoothConnectHandler extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(BluetoothConnectHandler.class);
	
	public static int runningThreadCount = 0;
	
    private StreamConnection streamConnection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    
    private boolean stopNow;
    	
    public interface Listener {
        public void onConnected();
        public void onDisconnected();
        public void onDataArrived(byte[] datas);
        public void onException(String description);
    }
    private Listener listener;

    public BluetoothConnectHandler(StreamConnection streamConnection, Listener listener){
    	this.listener = listener;
    	this.streamConnection = streamConnection;
    	stopNow = true;
    	
    	this.start();	
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(streamConnection.openOutputStream()));
			dataInputStream = new DataInputStream(new BufferedInputStream(streamConnection.openInputStream()));
		}catch(IOException e) {
			logger.error("run() => make data stream : " + e.toString());
    		if (listener != null) listener.onException(e.toString());
    		return;
		}
		
		if (listener != null) listener.onConnected();
		
		runningThreadCount++;		
		try {
			stopNow = false;
			while(!stopNow){
				int size = dataInputStream.available();
            	if (size > 0) {
            		byte[] readDatas = new byte[size];
            		dataInputStream.read(readDatas, 0, size);
    				logger.info("Received : " + new String(readDatas)); // debug     		
            		logger.debug("received datas : " + ModbusUtil.toHex(readDatas));
            		if (listener != null) listener.onDataArrived(readDatas);
            	}else if (size == 0) {
            		try {Thread.sleep(100);}catch(InterruptedException e) {}
            		continue;
            	}else{
            		logger.debug("disconnected.!");
            		break;
            	}
	        }
		}catch(Exception e) {
			logger.debug("run() => mDataInputStream Error!!! : " + e.toString());
		}finally {
			runningThreadCount--;
			logger.debug("thread end... sRunningThreadCount = " + runningThreadCount);
			
			if (dataInputStream != null) try{dataInputStream.close();}catch(Exception e){}finally{dataInputStream = null;}
	        if (dataOutputStream != null) try{dataOutputStream.close();}catch(Exception e){}finally{dataOutputStream = null;}
	        if (streamConnection != null) try{streamConnection.close();}catch(Exception e){}finally{streamConnection = null;}
	        
	        if (listener != null) listener.onDisconnected();
		}		
	}
	
	public boolean sendData(byte[] datas){
		if (!isConnected()) {
			logger.error("Error sendData : Disconnected!!!");
			return false;
		}
		
		try{
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			int offset = 0;
			int chunkSize;
			
			while (offset < datas.length) {
				chunkSize = Math.min(buffer.remaining(), datas.length - offset);
				buffer.put(datas, offset, chunkSize);
				
				offset += chunkSize;
				
				if (!buffer.hasRemaining()) {
					buffer.flip();
					byte[] sendDatas = new byte[buffer.remaining()];
			        buffer.get(sendDatas);
					
					dataOutputStream.write(sendDatas);
					dataOutputStream.flush();
					
					logger.info("sent datas : " + ModbusUtil.toHex(sendDatas));
					buffer.clear();
				}
			}
			
			if (buffer.position() > 0) {
				buffer.flip();
				byte[] sendDatas = new byte[buffer.remaining()];
		        buffer.get(sendDatas);
		        
				dataOutputStream.write(sendDatas);
				dataOutputStream.flush();
				
				logger.info("sent datas : " + ModbusUtil.toHex(sendDatas));
			}
			
			//dataOutputStream.write(datas);
			//dataOutputStream.flush();
			logger.info("sent total : " + datas.length);
        	return true;
		}catch(Exception e) {
			logger.error("mDataOutputStream Error!!! : ", e);
			disconnect();
		}
		return false;
	}
	
    public void disconnect(){
    	if (!stopNow) {
    		logger.info("disconnect...");
    		stopNow = true;            
            this.interrupt();
    	}
    }

	public boolean isConnected() {
		if (streamConnection == null || dataOutputStream == null || dataInputStream == null) return false;
		return true;
	}
	
}