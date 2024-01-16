package com.care4u.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ByteSocketHandler extends Thread{

	public static int DISCONNECTED = 0;
	public static int CONNECTING = 1;
	public static int CONNECTED = 2;
	
	private int mConnectionStatus;
	private String mHost;
	private int mPort;
	
	private Socket mSocket;
	private DataOutputStream mDataOutputStream;
	private DataInputStream mDataInputStream;
	
	public interface Listener{
		public void onDataArrived(ByteSocketHandler handler, byte[] data);
		public void onConnected(ByteSocketHandler handler);
		public void onDataSent(ByteSocketHandler handler, byte[] data);
		public void onException(ByteSocketHandler handler, String message);
		public void onClosed(ByteSocketHandler handler);
	}
	private final Listener mListener;

	public ByteSocketHandler(String host, int port, Listener listener) {
		// TODO Auto-generated constructor stub
		mListener = listener;
		mHost = host;
		mPort = port;
		mConnectionStatus = DISCONNECTED;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		mConnectionStatus = CONNECTING;
		try{			
			mSocket = new Socket(mHost, mPort);
			mDataOutputStream = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
			mDataInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));			
		}catch(Exception e){
			if (mListener != null) mListener.onException(this, "connect Server..." + e.toString());
			close();
			return;
		}

		mConnectionStatus = CONNECTED;
		if (mListener != null) mListener.onConnected(this);
		
		try{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			while(mConnectionStatus == CONNECTED){
				byteArrayOutputStream.reset();
				int bytesRead = -1;
				while((bytesRead = mDataInputStream.read(data)) != -1 ) {  
					byteArrayOutputStream.write(data, 0, bytesRead);  
				}
				byte[] readDatas = byteArrayOutputStream.toByteArray();
				if (mListener != null) mListener.onDataArrived(this, readDatas);
			}
		}catch(Exception e){
			if (mListener != null) mListener.onException(this, "read Data From client..." + e.toString());
		}
		close();
	}
	
	public int getConnectionStatus() {
		return mConnectionStatus;
	}

	public synchronized boolean send(String data){
		try{
			mDataOutputStream.write(data.getBytes("UTF-8"));
			mDataOutputStream.flush();
			if (mListener != null) mListener.onDataSent(this, data.getBytes("UTF-8"));
		}catch(Exception e){
			if (mListener != null) mListener.onException(this, "send Data To Server..." + e.toString());
			close();
			return false;
		}
		return true;
	}

	public void close(){
		mConnectionStatus = DISCONNECTED;
		if (mDataOutputStream != null) try{mDataOutputStream.close();}catch(Exception e){}finally{mDataOutputStream = null;}
		if (mDataInputStream != null) try{mDataInputStream.close();}catch(Exception e){}finally{mDataInputStream = null;}
		if (mSocket != null) try{mSocket.close();}catch(Exception e){}finally{mSocket = null;}
		if (mListener != null) mListener.onClosed(this);
	}
	
}
