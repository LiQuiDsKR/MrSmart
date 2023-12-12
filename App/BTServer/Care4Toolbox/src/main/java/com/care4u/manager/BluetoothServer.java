package com.care4u.manager;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothServer extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(BluetoothServer.class);
	
	private boolean stopNow;
	
	public interface Listener {
		public void onConnected(StreamConnection connection);
	}

	private Listener listener;

	public BluetoothServer(Listener listener) {
		if (listener == null) {
			logger.error("Listener is NULL : Stopped...");
			return;
		}
		
		this.listener = listener;
		
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
				listener.onConnected(connection);
			}
		} catch (Exception e) {
		    logger.error("Bluetooth Server Died...", e);
		}
		logger.info("Bluetooth Server End...");
		
	}

	public void destroy() {
		if (!stopNow) {
			stopNow = true;
			this.interrupt();
		}
	}

}
