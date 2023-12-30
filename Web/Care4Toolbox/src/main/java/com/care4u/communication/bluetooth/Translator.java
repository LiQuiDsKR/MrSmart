package com.care4u.communication.bluetooth;

import net.wimpi.modbus.util.ModbusUtil;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translator {
	
	private static final Logger logger = LoggerFactory.getLogger(Translator.class);
	
	private static final int INTEGER_SIZE = 4;
	
	private DataHolder mSizeDataHolder, mBodyDataHolder;
	private int mDataIndex;
	private boolean mReadSizeInfo, mReadBodyInfo;
	
	private final ByteArrayOutputStream byteArrayOutputStream;

	public interface Listener{
		public void onDataArrived(byte[] datas);
		public void onException(Exception ex);
	}
	private Listener mListener;

	public Translator(Listener listener) {
		mListener = listener;
		
		mSizeDataHolder = new DataHolder();
		mSizeDataHolder.datas = new byte[INTEGER_SIZE];
		
		mBodyDataHolder = new DataHolder();
		
		byteArrayOutputStream = new ByteArrayOutputStream();
		
		initialize();
	}
	
	public void initialize(){
		mBodyDataHolder.datas = null;
		
		mDataIndex = 0;

		mSizeDataHolder.index = 0;
		mBodyDataHolder.index = 0;

		mReadSizeInfo = false;
		mReadBodyInfo = false;
		
		byteArrayOutputStream.reset();
	}
	
	public byte[] process(byte[] datas){		
		try{
			logger.info("decode=" + ModbusUtil.toHex(datas));
			logger.info("decode=" + new String(datas, StandardCharsets.UTF_8));
			
			byteArrayOutputStream.write(datas);
			byteArrayOutputStream.flush();
			
			byte[] reveivedDatas = byteArrayOutputStream.toByteArray();

			if (!readSizeInfo(reveivedDatas)){
				return null;
			}
			
			if (!readBodyInfo(reveivedDatas)){
				return null;
			}

			if (mListener != null) mListener.onDataArrived(mBodyDataHolder.datas);
			
			if (mDataIndex < reveivedDatas.length){				
				byte[] nextDatas = new byte[datas.length - mDataIndex];
				System.arraycopy(datas, mDataIndex, nextDatas, 0, nextDatas.length);
					
				logger.info("start next datas...");				
				initialize();
				return nextDatas;
			}
		}catch(Exception ex){
			logger.error("decode : ", ex);
			if (mListener != null) mListener.onException(ex);
		}
		initialize();
		return null;
	}

	public byte[] getData() {
		return mBodyDataHolder.datas;
	}

	private boolean readSizeInfo(byte[] datas){
		if (mReadSizeInfo) return true;

		if (!readData(datas, mSizeDataHolder)){
			return false;
		}
		
		mReadSizeInfo = true;
		int length = byte2int(mSizeDataHolder.datas, 0);
		logger.info("readBodyDataSize=" + length + ", data index=" + mDataIndex);
		/*
		if (length > 1024) {
			logger.error("readSizeInfo : Too Long Data Size = " + length);
			if (mListener != null) mListener.onException(new Exception("readSizeInfo : Too Long Data Size = " + length));
			return false;
		}*/
		
		mBodyDataHolder.datas = new byte[length];
		return true;
	}
	
	private boolean readBodyInfo(byte[] datas){
		if (mReadBodyInfo) return true;
		
		if (!readData(datas, mBodyDataHolder)){
			return false;
		}
		
		mReadBodyInfo = true;
		logger.info("readBodyInfo size=" + mBodyDataHolder.datas.length + ", data index=" + mDataIndex);
		return true;
	}
	
	private boolean readData(byte[] srcDatas, DataHolder destDataHolder){
		if (destDataHolder.index >= destDataHolder.datas.length){
			return true;
		}
		
		if (mDataIndex >= srcDatas.length){
			return false;
		}
		
		if (srcDatas.length - mDataIndex < destDataHolder.datas.length - destDataHolder.index){
			System.arraycopy(srcDatas, mDataIndex, destDataHolder.datas, destDataHolder.index, (srcDatas.length - mDataIndex));
			destDataHolder.index += srcDatas.length - mDataIndex;
			mDataIndex += srcDatas.length - mDataIndex;
			return false;
		}
		
		int length = destDataHolder.datas.length - destDataHolder.index;
		System.arraycopy(srcDatas, mDataIndex, destDataHolder.datas, destDataHolder.index, length);
		destDataHolder.index += length;
		mDataIndex += length;
		return true;
	}
	
	public static int byte2int(byte[] buffer, int startIndex){
		byte[] datas = new byte[INTEGER_SIZE];
		System.arraycopy(buffer, startIndex, datas, 0, INTEGER_SIZE);
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(INTEGER_SIZE);
		byteBuffer = ByteBuffer.wrap(datas);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		//byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		return byteBuffer.getInt();
	}
	
	private class DataHolder{
		public int index;
		public byte[] datas;
	}
}
