package com.care4u.communication.object;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ObjectSender {

	// 객체를 나누어서 전송하는 예시 코드
	public static boolean sendObject(SocketChannel socketChannel, Serializable object) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
		
			byte[] objectData = byteArrayOutputStream.toByteArray();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			int offset = 0;
			int chunkSize;
			
			// 객체 데이터를 나누어서 버퍼에 쓰기
			while (offset < objectData.length) {
				chunkSize = Math.min(buffer.remaining(), objectData.length - offset);
				buffer.put(objectData, offset, chunkSize);
				
				offset += chunkSize;
				
				// 버퍼를 전송 또는 처리
				if (!buffer.hasRemaining()) {
					buffer.flip();
					socketChannel.write(buffer);
					buffer.clear();
				}
			}
			
			// 마지막으로 남은 부분 전송
			if (buffer.position() > 0) {
				buffer.flip();
				socketChannel.write(buffer);
			}
		} catch(IOException ie) {
			System.out.println(ie.toString());
			return false;
		} finally {
			try {if (objectOutputStream != null) objectOutputStream.close();} catch(IOException ie) {}
			try {if (byteArrayOutputStream != null) byteArrayOutputStream.close();} catch(IOException ie) {}
		}
	    return true;
	}

	public static void main(String[] args) {
		try {
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 3232));

			System.out.println("Server Socket Opened...");
			SocketChannel socketChannel = serverSocket.accept();			
			sendObject(socketChannel, new ExampleObject("Hello, Client!"));
			
			// 소켓 닫기
			socketChannel.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
