package com.care4u.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.care4u.common.GlobalConstants;

final public class ChannelHandler implements ReadWriteSelectorHandler, ConnectorSelectorHandler {

	private static final Logger mLog = LoggerFactory.getLogger(ChannelHandler.class);
	
	private SelectorThread mSelectThread;
	
	private SocketChannel mSocketChannel;
	
	private String mIpaddress = "0.0.0.0";
	private byte[] mWriteData;
	
	private long mCommunicationTime;
	
	public interface Listener{
		public void onConnected(SocketChannel channel);
		public void onDataArrived(ByteBuffer data);
		public void onDataSent(int size);
		public void onSocketException(Exception ex);
		public void onSocketDisconnected();
	}
	private Listener mListener;
	
	public ChannelHandler(Listener listener){
		mListener = listener;
	}
	
	public void initialize(SelectorThread selectThread, SocketChannel socketChannel){
		//server mode
		mSocketChannel = socketChannel;
		
		mWriteData = null;		
		mCommunicationTime = Calendar.getInstance().getTimeInMillis();
		
		try{
			mIpaddress = mSocketChannel.getRemoteAddress().toString();
			mLog.info("[" + mIpaddress + "] initialize... SelectorThread");
			
			//mSocketChannel.socket().setReceiveBufferSize(mBufferSize);      
			//mSocketChannel.socket().setSendBufferSize(mBufferSize);			
		}catch(Exception ex){
			mLog.error("[" + mIpaddress + "] initialize... ", ex);
			if (mListener != null){mListener.onSocketException(ex);}
			closeChannel();
			return;
		}

		mSelectThread = selectThread;
		mSelectThread.registerChannelLater(mSocketChannel, SelectionKey.OP_READ,  this,
				new CallbackErrorHandler() {
					public void handleError(Exception ex) {
						mLog.error("[" + mIpaddress + "] mSelectThread.registerChannelLater()... ", ex);
						if (mListener != null){mListener.onSocketException(ex);}
						closeChannel();						
					}
				}
		);
		
		if (mListener != null) mListener.onConnected(mSocketChannel);
	}
	
	@Override
	public void closeChannel(){
		if (mSocketChannel != null){
			try {
				mSocketChannel.close();
			} catch (Exception e) {
				mLog.error("[" + mIpaddress + "] mSocketChannel.close()... ", e);
				if (mListener != null){mListener.onSocketException(e);}
			}
		}
		
		if (mSelectThread != null && mSocketChannel != null){
			try {
				mSelectThread.removeChannelNow(mSocketChannel);
			} catch (Exception e) {
				mLog.error("[" + mIpaddress + "] mSelectThread.removeChannelNow()... ", e);
				if (mListener != null) mListener.onSocketException(e);
			}
		}
		
		mSocketChannel = null;
		
		mSelectThread = null;
		
		mIpaddress = "0.0.0.0";
		
		mCommunicationTime = 0;
		
		if (mListener != null) mListener.onSocketDisconnected();
	}

	public void send(byte[] datas){
		mWriteData = datas;
		
		if (mSelectThread != null) {
			mSelectThread.addChannelInterestLater(mSocketChannel, SelectionKey.OP_WRITE, new CallbackErrorHandler() {
				public void handleError(Exception ex) {
					mLog.error("mSelectThread.SelectionKey.OP_WRITE... ", ex);
					if (mListener != null){mListener.onSocketException(ex);}
					closeChannel();
				}
			});
		}
	}
	
	@Override
	public void handleConnect() {
		// TODO Auto-generated method stub
		mLog.debug("handleConnect... " + mIpaddress);
		try {
			//mSocketChannel.socket().setReceiveBufferSize(mBufferSize);      
			//mSocketChannel.socket().setSendBufferSize(mBufferSize);
			if (mListener != null) mListener.onConnected(mSocketChannel);
		}catch(Exception ex){
			mLog.error("[" + mIpaddress + "] mSelectThread.addChannelInterestNow... ", ex);
			if (mListener != null){mListener.onSocketException(ex);}
			closeChannel();
		}
	}

	@Override
	public void onConnectFailed() {
		// TODO Auto-generated method stub
		mLog.debug("mSelectThread.onConnectFailed... " + mIpaddress);
		if (mListener != null){mListener.onSocketException(new Exception("mSelectThread.onConnectFailed... " + mIpaddress));}
		closeChannel();
	}

	@Override
	public void handleWrite() {
		if (mWriteData == null){
				return;
		}

		if (mSocketChannel == null) return;
		
		mLog.debug("handleWrite...isOpen=" + mSocketChannel.isOpen() + ", isConnected=" + mSocketChannel.isConnected() + ", isRegistered=" + mSocketChannel.isRegistered());
    	if (!mSocketChannel.isConnected()) {
    		return;
    	}
    	
    	try {
    		int bytes = mSocketChannel.write(ByteBuffer.wrap(mWriteData));
    		mListener.onDataSent(bytes);
    		mCommunicationTime = Calendar.getInstance().getTimeInMillis();
    	} catch (Exception e) {
			mLog.debug("handleWrite:", e);
			if (mListener != null){mListener.onSocketException(e);}
			closeChannel();
			return;
		}
		
    	if (mSelectThread != null) {
			mSelectThread.addChannelInterestLater(mSocketChannel, SelectionKey.OP_READ, new CallbackErrorHandler() {
				public void handleError(Exception ex) {
					mLog.error("mSelectThread.SelectionKey.OP_READ... ", ex);
					if (mListener != null){mListener.onSocketException(ex);}
					closeChannel();
				}
			});
    	}
		
	}

	@Override
	public void handleRead() {
		if (mSocketChannel == null || !mSocketChannel.isConnected()) {
			return;
		};
		
		ByteBuffer readBuffer = ByteBuffer.allocateDirect(25);
		readBuffer.clear();
		
		int readBytes = 0;
		try {
			readBytes = mSocketChannel.read(readBuffer);
			mCommunicationTime = Calendar.getInstance().getTimeInMillis();
		} catch (Exception ex) {
			mLog.debug("handleRead() => 1 : ", ex);
			if (mListener != null){mListener.onSocketException(ex);}
			closeChannel();						
		}
		//-1, indicating EOS, meaning you should close the channel
		//zero, meaning there was no data to read, meaning you should return to the select() loop, and
		//a positive value, meaning you have read that many bytes, which you should then extract and remove from the ByteBuffer (get()/compact()) before continuing.
		if (readBytes == -1) {
			mLog.error("handleRead() : readBytes == -1");
			closeChannel();
		}else if (readBytes == 0) {        
			mLog.debug("handleRead() : readBytes == 0"); 
			closeChannel();
		}else{
			mLog.debug("handleRead() : readBytes = " + readBytes);
			mListener.onDataArrived(readBuffer);
		}
	}
	
	public boolean isValidConnection(){
		long diff = Calendar.getInstance().getTimeInMillis() - mCommunicationTime;
		if (diff > GlobalConstants.COMMUNICATION_TIME_LIMIT){
			return false;			
		}
		return true;
	}
	

/*
	private void disableReading() throws IOException {
		selector.removeChannelInterestNow(socketChannel, SelectionKey.OP_READ);    
	}
*/  
}