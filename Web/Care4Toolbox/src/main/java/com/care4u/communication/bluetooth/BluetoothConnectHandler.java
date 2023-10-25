package com.care4u.communication.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.care4u.common.GlobalConstants;

import net.wimpi.modbus.util.ModbusUtil;

public class BluetoothConnectHandler extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(BluetoothConnectHandler.class);
	
	public static int runningThreadCount = 0;
	
    private StreamConnection streamConnection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private long sendingSeconds;
    private boolean stopNow;
    
    private Timer timer;
	private final TimerTask timerTask = new TimerTask(){
		@Override
		public void run() {
			// TODO Auto-generated method stub			
			if (sendingSeconds > 0 && isConnected()) {
				if (sendingSeconds > GlobalConstants.COMMUNICATION_TIMEOUT) {
					if (listener != null) listener.onException("COMMUNICATION_TIMEOUT");
					disconnect();
				}				
				sendingSeconds++;
			}
		}
	};
    	
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
    		disconnect();
    		return;
		}
		
		if (listener != null) listener.onConnected();
		
		runningThreadCount++;
		sendingSeconds = 0;
		timer = new Timer();
		timer.schedule(timerTask, 1000, 1000);
		
		try {
			stopNow = false;
			while(!stopNow){
				int size = dataInputStream.available();
            	if (size > 0) {
            		byte[] readDatas = new byte[size];
            		dataInputStream.read(readDatas, 0, size);
            		
            		sendingSeconds = 0;        		
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
			
			disconnect();
			timer.cancel();
		}		
	}

	public boolean sendData(byte[] datas){		
		try{
        	dataOutputStream.write(datas);
        	dataOutputStream.flush();
        	sendingSeconds++;
        	logger.debug("sent datas : " + ModbusUtil.toHex(datas));
        	return true;
		}catch(Exception e) {
			logger.error("mDataOutputStream Error!!! : ", e);
			disconnect();
		}
		return false;
	}
	
    public void disconnect(){
    	if (timer != null) {
    		timer.cancel();
    		timer = null;
    	}
    	
    	if (!stopNow) {
    		logger.info("disconnect...");
    		stopNow = true;
            sendingSeconds = 0;
            
            this.interrupt();
            if (listener != null) listener.onDisconnected();
    	}
    	
        if (dataInputStream != null) try{dataInputStream.close();}catch(Exception e){}finally{dataInputStream = null;}
        if (dataOutputStream != null) try{dataOutputStream.close();}catch(Exception e){}finally{dataOutputStream = null;}
        if (streamConnection != null) try{streamConnection.close();}catch(Exception e){}finally{streamConnection = null;}
    }

	public boolean isConnected() {
		if (streamConnection == null || dataOutputStream == null || dataInputStream == null) return false;
		return true;
	}
	
}