package com.care4u.communication.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ObjectReceiver {
	
	public static Object receiveObject(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteArrayOutputStream = null;
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		Object object = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			int bytesRead = 0;
			while ((bytesRead = socketChannel.read(buffer)) > 0) {
			    buffer.flip();
			
			    // 버퍼에서 읽은 데이터를 ByteArrayOutputStream에 쓰기
			    byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());			
			    buffer.clear();
			}
			byteArrayOutputStream.flush();
			
			byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());	
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			object = objectInputStream.readObject();				
			
        } catch(IOException ie) {
			System.out.println(ie.toString());
		} finally {
			try {if (objectInputStream != null) objectInputStream.close();} catch(IOException ie) {}
			try {if (byteArrayInputStream != null) byteArrayInputStream.close();} catch(IOException ie) {}
			try {if (byteArrayOutputStream != null) byteArrayOutputStream.close();} catch(IOException ie) {}
		}
		return object;
    }
	
	public static void main(String[] args) {
		try {
			// InetSocketAddress 생성
			InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 3232);
			
			// SocketChannel 열고 서버에 연결
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(socketAddress);
			
			// 서버에서 객체 수신 예제
			Object receivedObject = receiveObject(socketChannel);
			if (receivedObject instanceof ExampleObject) {
				ExampleObject exampleObject = (ExampleObject) receivedObject;
				System.out.println("Received: " + exampleObject.getData());
			}
			
			// 소켓 닫기
			socketChannel.close();
		} catch (IOException | ClassNotFoundException e) {
		    e.printStackTrace();
		}
	}
	
	
}
