package com.care4u.nio;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.care4u.common.GlobalConstants;

public class ConnectHandler extends Thread{
    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private boolean mStopNow;

    private final Logger mLogger = LoggerFactory.getLogger(ConnectHandler.class);
    	
    public interface Listener{
        public void onConnected();
        public void onDisconnected();
        public void onDataArrived(byte[] datas);
        public void onDataSent(byte[] datas);
        public void onException(String description);
    }
    private Listener mListener;

    public ConnectHandler(String ipaddress, int port, Listener listener){
        mListener = listener;
        if (!connectServer(ipaddress, port)){
        	mLogger.error("서버 연결 에러!!! 네트워크를 확인하세요..." + ipaddress + ":" + port);
			return;
		}
        this.start();
    }

    @Override
	public void run() {
		// TODO Auto-generated method stub
    	mStopNow = true;
		try{
	    	mInputStream = mSocket.getInputStream();
	    	mOutputStream = mSocket.getOutputStream();
	    	byte[] readDatas = new byte[GlobalConstants.SOCKET_BUFFER_SIZE];
	    	
	    	mLogger.debug("start thread ok...");
	    	if (mSocket.isConnected()){
	    		mStopNow = false;
	    		mLogger.debug("Server Connected OK...");
	    		if (mListener != null) mListener.onConnected();
            }else{
            	mLogger.error("서버 연결 에러!!! 네트워크를 확인하세요...1");
            }
	    	
            while(!Thread.interrupted() && !mStopNow){
            	int size = mInputStream.read(readDatas);
            	if (size > 0){
            		byte[] datas = new byte[size];
            		System.arraycopy(readDatas, 0, datas, 0, datas.length);
            		if (mListener != null) mListener.onDataArrived(datas);
            	}else if (size == 0){
            		continue;
            	}else{
            		mLogger.debug("연결이 끊어졌습니다.! : " + size);
            		break;
            	}
            }
        }catch(Exception ex){
        	mLogger.error("서버 연결 에러!!! 네트워크를 확인하세요...2" + ex.toString());
        	if (mListener != null) mListener.onException("서버 연결 에러!!! 네트워크를 확인하세요...2");
        }
		disconnect();
	}

	public void sendData(byte[] datas){
		if (mOutputStream == null){
			if (mListener != null) mListener.onException("mOutputStream is null...네트워크 연결을 확인하세요.");
			return;
		}
        try{
        	mOutputStream.write(datas);
        	mOutputStream.flush();
        	if (mListener != null) mListener.onDataSent(datas);
        }catch(Exception ex){
        	mLogger.error("데이터 전송 오류입니다...!" + ex.toString());
        	if (mListener != null) mListener.onException("데이터 전송 오류입니다...!");            
        }
    }

    public void disconnect(){
        mStopNow = true;
        if (mInputStream != null) try{mInputStream.close();}catch(Exception e){}finally{mInputStream = null;}
        if (mOutputStream != null) try{mOutputStream.close();}catch(Exception e){}finally{mOutputStream = null;}
        if (mSocket != null) try{mSocket.close();}catch(Exception e){}finally{mSocket = null;}
        Thread.interrupted();
        if (mListener != null) mListener.onDisconnected();
    }
    
    private boolean connectServer(String ipaddress, int port){
		try {
			mLogger.debug("connect Server..." + ipaddress + ":" + port);
    		mSocket = new Socket(ipaddress, port);
    		/*
    		SocketAddress socketAddress = new InetSocketAddress(GlobalConstants.sServerIpaddress, GlobalConstants.SERVER_PORT);  
    		mSocket = new Socket();
    		mSocket.setSoTimeout(GlobalConstants.CONNECTION_TIMEOUT & 1000);	//InputStream에서 데이터읽을때의 timeout
    		mSocket.connect(socketAddress, GlobalConstants.CONNECTION_TIMEOUT & 1000);	//socket 연결 자체에대한 timeout
    		*/
    	} catch (UnknownHostException e) {    		
    		mLogger.error("잘못된 서버 주소입니다..." + ipaddress);
    		if (mListener != null) mListener.onException("잘못된 서버 주소입니다..." + ipaddress);
            return false;
    	} catch (Exception e) {    		
    		mLogger.error("서버가 응답하지 않습니다..." + ipaddress);
    		if (mListener != null) mListener.onException("서버가 응답하지 않습니다..." + ipaddress);
            return false;
    	}
    	return true;
    }

	public boolean isConnected(){
		if (mSocket == null) return false;
		return mSocket.isConnected();
	}
}