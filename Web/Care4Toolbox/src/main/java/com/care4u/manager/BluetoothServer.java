package com.care4u.manager;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.care4u.communication.bluetooth.BluetoothCommunicationHandler;

public class BluetoothServer extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(BluetoothServer.class);
	
	private List<BluetoothCommunicationHandler> workersPool = new CopyOnWriteArrayList<BluetoothCommunicationHandler>();
	
	private BluetoothCommunicationHandler.Listener bluetoothCommunicationHandlerListener = new BluetoothCommunicationHandler.Listener() {
		
		@Override
		public void onException(BluetoothCommunicationHandler handler, String message) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDisconnected(BluetoothCommunicationHandler handler) {
			// TODO Auto-generated method stub
			workersPool.remove(handler);
			logger.info("workersPool size : " + workersPool.size() + ", Bye : " + handler.getMacaddress());
		}
		
		@Override
		public void onDataSent(BluetoothCommunicationHandler handler, int size, String data) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDataArrived(BluetoothCommunicationHandler handler, int size, String data) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnected(BluetoothCommunicationHandler handler) {
			// TODO Auto-generated method stub
			workersPool.add(handler);
			logger.info("workersPool size : " + workersPool.size() + ", Hello : " + handler.getMacaddress());
		}
	};
	
	private boolean stopNow;

	public BluetoothServer() {
		this.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// Bluetooth 디바이스를 검색합니다.
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			logger.info("Bluetooth Adapter: " + localDevice.getBluetoothAddress());
			
			// 서버 소켓을 만듭니다.
			String url = "btspp://localhost:" + new UUID("0000110100001000800000805F9B34FB", false).toString() + ";name=BluetoothServer";
			StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(url);
			
			logger.info("Bluetooth Server 시작...");
			int count = 1;
			stopNow = false;
			while (!stopNow) {
				// 클라이언트 연결을 대기합니다.
				StreamConnection connection = notifier.acceptAndOpen();
				logger.debug("클라이언트 연결됨: " + connection.toString());
				
				// 데이터를 주고받을 스레드를 시작합니다.
				new BluetoothCommunicationHandler("macaddress" + count, connection, bluetoothCommunicationHandlerListener);
				count++;
			}
		} catch (Exception e) {
		    logger.error("Bluetooth Server Died...", e);
		}
	}

	public void destroy() {
		if (!stopNow) {
			stopNow = true;
			this.interrupt();
		}
		
		Iterator<BluetoothCommunicationHandler> iterator = workersPool.iterator();
		while(iterator.hasNext()) {
			iterator.next().destroy();
		}
	}

}
