package com.care4u.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Acceptor implements AcceptSelectorHandler {

private static final Logger mLog = LoggerFactory.getLogger(Acceptor.class);
	
	private ServerSocketChannel mServerSocketChannel; 
	private SelectorThread mIOThread;
	private final int mListenPort;
	private boolean mAsciiMode;
	private boolean mStop;
	
	private SelectorThread.Listener mSelectorThreadListener = new SelectorThread.Listener() {		
		@Override
		public void onDied(SelectorThread selector, String message) {
			// TODO Auto-generated method stub
			mLog.error("Acceptor's SelectorThread is Died..." + message);
			if (mIOThread != null) mIOThread.requestClose();
			
			try{Thread.sleep(60 * 1000);}catch(InterruptedException e) {}
			
			mIOThread = new SelectorThread(mSelectorThreadListener);			
		}
	};
	
	public interface Listener{
		public void socketConnected(Acceptor acceptor, SocketChannel sc, boolean asciiMode);
		public void socketError(Acceptor acceptor, Exception ex);
	}
	private Listener mListener;
		
	public Acceptor(int listenPort, boolean asciiMode, Listener listener) { 
		mListenPort = listenPort;
		mAsciiMode = asciiMode;
		mListener = listener;
		mIOThread = new SelectorThread(mSelectorThreadListener);
		mStop = false;
	}

	public void openServerSocket() throws IOException {
		mServerSocketChannel = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(mListenPort);
		mServerSocketChannel.socket().bind(isa, 100);

		// This method might be called from any thread. We must use 
		// the xxxLater methods so that the actual register operation
		// is done by the selector's thread. No other thread should access
		// the selector directly.
		mIOThread.registerChannelLater(mServerSocketChannel, SelectionKey.OP_ACCEPT, this,
			new CallbackErrorHandler() {
				public void handleError(Exception ex) {    
					mListener.socketError(Acceptor.this, ex);
					close();
				}
			}
		);
		mLog.info("openServerSocket() : Listen Port = " + mListenPort);
	}

	public String toString() {  
		return "ListenPort: " + mListenPort;
	}

	public void handleAccept() {
		SocketChannel socketChannel = null;
		try {
			socketChannel = mServerSocketChannel.accept();
			// Reactivate interest to receive the next connection. We
			// can use one of the XXXNow methods since this method is being
			// executed on the selector's thread.
			mIOThread.addChannelInterestNow(mServerSocketChannel, SelectionKey.OP_ACCEPT);
			if (socketChannel != null && !mStop) {
				// Connection established
				mListener.socketConnected(this, socketChannel, mAsciiMode);
			}else{
				mListener.socketError(this, new Exception("Accepted...but SocketChannel is null !!!"));
			}
		} catch (IOException e) {
			mListener.socketError(this, e);
			close();
		}		
	}
	
	public void close()  {
		try {
			mStop = true;
			mIOThread.removeChannelInterestNow(mServerSocketChannel, SelectionKey.OP_ACCEPT);
			if (mServerSocketChannel != null) {
				try {
					mServerSocketChannel.close();
				} catch (IOException e) {
					// Ignore
				}
			}
			mIOThread.requestClose();
			
/*			
			// Must wait for the socket to be closed.
			ioThread.invokeAndWait(
					new Runnable() {      
						public void run() {
							if (ssc != null) {
								try {
									ssc.close();
								} catch (IOException e) {
									// Ignore
								}
							}
						}
					}
			);
*/
		} catch (Exception e) {
			// Ignore
		}
	}

	public boolean isStop() {
		return mStop;
	}
}