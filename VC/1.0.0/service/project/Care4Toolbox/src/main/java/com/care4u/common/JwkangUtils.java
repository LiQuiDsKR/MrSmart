package com.care4u.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class JwkangUtils {

	public final static int INTEGER_SIZE = 4;
	public final static int LONG_SIZE = 8;
	
	public static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);	
	public static final SimpleDateFormat sSimpleTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
	private static final SimpleDateFormat sSimpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.KOREA);
	
	public static Properties loadProperties(String path){
		Properties properties = new Properties();
		try{			
			FileInputStream fileInputStream = new FileInputStream(path);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
	        
			properties.load(bufferedInputStream);
	        
	        if (bufferedInputStream != null) bufferedInputStream.close();
	        if (fileInputStream != null) fileInputStream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return properties;
	}
	
	public static String getDateString(Calendar calendar) {
	    String dateString = "1970-01-01";
	    try {
	    	TimeZone tz = new SimpleTimeZone( 9 * 60 * 60 * 1000, "KST" );
	    	TimeZone.setDefault(tz);
	    	dateString = sSimpleDateFormat.format(calendar.getTime());
	    } catch(Exception e) {}
	    return dateString;
	}
	
	public static String getTimeString(Calendar calendar) {
	    String timeString = "00:00:00";
	    try {
	    	TimeZone tz = new SimpleTimeZone( 9 * 60 * 60 * 1000, "KST" );
	    	TimeZone.setDefault(tz);
	    	timeString = sSimpleTimeFormat.format(calendar.getTime());
	    } catch(Exception e) {}
	    return timeString;
	}
	
	public static String getMonthString() {
	    String dateString = "1970-01";
	    try {
	    	TimeZone tz = new SimpleTimeZone( 9 * 60 * 60 * 1000, "KST" );
	    	TimeZone.setDefault(tz);
	    	dateString = sSimpleMonthFormat.format(new Date());
	    } catch(Exception e) {}
	    return dateString;
	}
	
	public String pasing(String data) {
		try {
			data = new String(data.getBytes("8859_1"), "EUC-KR");
		}catch (Exception e){ }
		return data;
	}

	public static byte[] int2byte(int val){
		ByteBuffer buffer = ByteBuffer.allocate(INTEGER_SIZE);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(val);		
		return buffer.array();
	}    
	public static int byte2int(byte[] val){
		ByteBuffer buffer = ByteBuffer.allocate(INTEGER_SIZE);
		buffer = ByteBuffer.wrap(val);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.getInt();
	}
	
	public static int byte2int(byte[] buffer, int startIndex){
		byte[] datas = new byte[INTEGER_SIZE];
		System.arraycopy(buffer, startIndex, datas, 0, INTEGER_SIZE);
	     return byte2int(datas);
	}
	
	public static byte[] ascii2byte(byte[] data) {
	    char ascii = 0;
	    for (int i = 0; i < data.length; ++ i) {
	    	ascii = (char) data[i];
	        data[i] = (byte) ascii;
	    }
	    return data;
	}
	
	public static int binaryStringToDecimal(String biString){
		int n = biString.length();      
		int decimal = 0;
		for (int d = 0; d < n; d++){
			decimal = decimal << 1;
			if (biString.charAt(d) == '1'){
				decimal = decimal | 1; // e.g. dec = 110 | (00)1 = 111
			}
		}
		return decimal;
	}
	
	public static byte[] long2byte(long val){
		ByteBuffer buffer = ByteBuffer.allocate(LONG_SIZE);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putLong(val);
		return buffer.array();
	}
	
	public static long byte2long(byte[] val){
		ByteBuffer buffer = ByteBuffer.allocate(LONG_SIZE);
		buffer = ByteBuffer.wrap(val);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.getLong();
	}
	
	public static long byte2long(byte[] buffer, int startIndex){
		byte[] datas = new byte[LONG_SIZE];
		System.arraycopy(buffer, startIndex, datas, 0, LONG_SIZE);		
	     return byte2long(datas);
	}
	
	public static byte[] short2byte(short s1){
		byte[] tempByte = new byte[2];
		tempByte[0] = (byte)((s1&0xFF00)>>8);
		tempByte[1] = (byte)((s1&0x00FF)>>0);
	 
		return tempByte; 
	}
	public static short byte2short(byte firstByte, byte secondByte){
		short tempShort = 0;
		tempShort |= (firstByte & (short)0xFF)<<8;
		tempShort |= (secondByte & (short)0xFF)<<0;
	 
		return tempShort;
	}
	
	public static int byte2UnsignedShort(byte firstByte, byte secondByte){
        return (0xffff & byte2short(firstByte, secondByte));
	}
	
	public static byte hexStringToByteArray(char highChar, char lowChar) throws IllegalArgumentException {
		int h = hexToBin(highChar);
		int l = hexToBin(lowChar);
		if( h==-1 || l==-1 )
			throw new IllegalArgumentException("contains illegal character for hexBinary: ");
		return (byte)(h*16+l);
	}
	
	private static int hexToBin( char ch ) {
	    if( '0'<=ch && ch<='9' )    return ch-'0';
	    if( 'A'<=ch && ch<='F' )    return ch-'A'+10;
	    if( 'a'<=ch && ch<='f' )    return ch-'a'+10;
	    return -1;
	}
	
	public static String byteArrayToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder(); 
		for(byte b : bytes){ 
			sb.append(String.format("%02X", b&0xff));
		}
		return sb.toString(); 
	}
	
	public static boolean isDisitString(String str){
		for (int i=0; i<str.length(); i++){
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public static void copyFile(File sourceF, File targetFile) {
    	try {
    		FileInputStream fin = new FileInputStream( sourceF.getAbsolutePath() );
    		FileOutputStream fout = new FileOutputStream( targetFile.getAbsolutePath() );

    		FileChannel inc = fin.getChannel();
    		FileChannel outc = fout.getChannel();
    		long size = inc.size();
    		   
    		inc.transferTo(0, size, outc);
    		   
    		outc.close();
    		inc.close();
    		   
    		fout.close();
    		fin.close();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
    public static byte[] object2byteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

    public static Object byteArray2Object(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }

    public static String toString(byte[] bytes) {
        return new String(bytes);
    }
    
    public static String getBits( byte inByte ){
        // Go through each bit with a mask
        StringBuilder builder = new StringBuilder();
        for ( int j = 0; j < 8; j++ ){
            // Shift each bit by 1 starting at zero shift
            byte tmp =  (byte) ( inByte >> j );

            // Check byte with mask 00000001 for LSB
            int expect1 = tmp & 0x01; 
            builder.append(expect1);
        }
        return ( builder.reverse().toString() );
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }
}
