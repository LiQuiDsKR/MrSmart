package com.care4u.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SelectorThread implements Runnable {
	
	public int handlerCount;
	public int selectorIndex;
	
	private Selector selector;

	private Thread selectorThread;
  
	private boolean closeRequested = false;
  
	private final List<Runnable> pendingInvocations = new ArrayList<Runnable>(32);

	private final Logger log = LoggerFactory.getLogger(SelectorThread.class);
	
	public interface Listener{
		public void onDied(SelectorThread selector, String message);
	}
	private Listener mListener;
	
	public SelectorThread(Listener listener) {
		mListener = listener;
		
		try {
			selector = Selector.open();
			selectorThread = new Thread(this);   
			selectorThread.start();
		}catch(IOException e) {
			if (mListener != null) mListener.onDied(SelectorThread.this, e.toString());
		}
	}
  
	public void requestClose() {
		closeRequested = true;
		selector.wakeup();
	}

	public void addChannelInterestNow(SelectableChannel channel, int interest) throws IOException {
		SelectionKey sk = channel.keyFor(selector);
		changeKeyInterest(sk, sk.interestOps() | interest);
	}

	public void addChannelInterestLater(final SelectableChannel channel, final int interest, final CallbackErrorHandler errorHandler) {
		invokeLater(new Runnable() {
			public void run() {
				try {
					addChannelInterestNow(channel, interest);
				} catch (IOException e) {
					errorHandler.handleError(e);					
				}
			}
		});
	}
  
	public void removeChannelInterestNow(SelectableChannel channel, int interest) throws IOException {
		SelectionKey sk = channel.keyFor(selector);
		changeKeyInterest(sk, sk.interestOps() & ~interest);
	}

	public void removeChannelInterestLater(final SelectableChannel channel, final int interest, final CallbackErrorHandler errorHandler)  {
		invokeLater(new Runnable() {
			public void run() {
				try {
					removeChannelInterestNow(channel, interest);
				} catch (IOException e) {
					errorHandler.handleError(e);					
				}
			}
		});
	}

	private void changeKeyInterest(SelectionKey sk, int newInterest) throws IOException {
		try {
			sk.interestOps(newInterest);
		} catch (CancelledKeyException cke) {
			IOException ioe = new IOException("Failed to change channel interest.");
			ioe.initCause(cke);
			throw ioe;
		}
	}

	public void registerChannelLater(final SelectableChannel channel, final int selectionKeys, final SelectorHandler handlerInfo, final CallbackErrorHandler errorHandler) {
		invokeLater(new Runnable() {
			public void run() {
				try {
					registerChannelNow(channel, selectionKeys, handlerInfo);
				} catch (IOException e) {					
					errorHandler.handleError(e);
				}
			}
		});
	}

	public void registerChannelNow(SelectableChannel channel, int selectionKeys, SelectorHandler handlerInfo) throws IOException {
		if (!channel.isOpen()) {
			throw new IOException("Channel is not open.");
		}
    
		try {
			if (channel.isRegistered()) {
				SelectionKey sk = channel.keyFor(selector);
				if (sk != null){
					log.error("registerChannelNow : Channel is already registered with other selector");
				}
				log.debug("registerd Channel...");
				sk.interestOps(selectionKeys);
				Object previousAttach = sk.attach(handlerInfo);
				assert previousAttach != null;
			} else {
				log.debug("regist new Channel...");
				channel.configureBlocking(false);
				channel.register(selector, selectionKeys, handlerInfo);
			}
		} catch (Exception e) {
			IOException ioe = new IOException("Error registering channel.");
			ioe.initCause(e);
			throw ioe;
		}
	}
	
	public void removeChannelNow(SelectableChannel channel) throws IOException{ 
		try {
			if (channel.isRegistered()) {
				SelectionKey sk = channel.keyFor(selector);				
				sk.attach(null);
				Object previousAttach = sk.attachment();
				if (previousAttach != null){
					log.error("removeChannelNow : Handle Info is Not Removed!!!");
				}
	        }else{
	        	log.error("removeChannelNow : Socket Channel is Not Registered!!!");
	        }
		} catch (Exception e) {
			IOException ioe = new IOException("Error remove channel.");
			ioe.initCause(e);
			throw ioe;      
		}  
	}
	
	public void invokeLater(Runnable run) {
		synchronized (pendingInvocations) {
			pendingInvocations.add(run);
		}
		selector.wakeup();
	}

	public void invokeAndWait(final Runnable task) throws InterruptedException {
		if (Thread.currentThread() == selectorThread) {
			task.run();      
		} else {
			final Object latch = new Object();
			synchronized (latch) {
				this.invokeLater(new Runnable() {
					public void run() {
						task.run();
						latch.notify();
					}
				});
				latch.wait();
			}
		}
	}

	private void doInvocations() {
		synchronized (pendingInvocations) {
			for (int i = 0; i < pendingInvocations.size(); i++) {
				Runnable task = (Runnable) pendingInvocations.get(i);
				task.run();
			}
			pendingInvocations.clear();
		}
	}

	public void run() {
		while (true) {   
			doInvocations();
			if (closeRequested) {
				return;
			}

			int selectedKeys = 0;
			try {
				selectedKeys = selector.select(500);
			} catch (IOException ioe) {
				log.error("selectedKeys = selector.select(500)", ioe);
				continue;
			}
			

			if (selectedKeys == 0) {
				continue;
			}
			
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey sk = it.next();
				it.remove();
				try {
					int readyOps = sk.readyOps();
					sk.interestOps(sk.interestOps() & ~readyOps);
					SelectorHandler handler = (SelectorHandler) sk.attachment();
					if (sk.isAcceptable()) {
						((AcceptSelectorHandler)handler).handleAccept();
					} else if (sk.isConnectable()) {
						((ConnectorSelectorHandler)handler).handleConnect();
					} else {
						ReadWriteSelectorHandler rwHandler = (ReadWriteSelectorHandler)handler; 
						if (sk.isReadable()) {
							rwHandler.handleRead();              
						}            
						if (sk.isValid() && sk.isWritable()) {
							rwHandler.handleWrite();
						}
					}
				} catch (Exception ex) {
					log.error("ERROR!!!  Selector is Died!!!!", ex);
					closeSelectorAndChannels();
					if (mListener != null) mListener.onDied(SelectorThread.this, ex.toString());
					return;
				}
			}
		}
	}

	private void closeSelectorAndChannels() {
		Set<SelectionKey> keys = selector.keys();
		for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext();) {
			SelectionKey key = iter.next();
			try {
				key.channel().close();
			} catch (IOException e) {
				// Ignore
			}
		}
		try {
			selector.close();
		} catch (IOException e) {
			// Ignore
		}
	}
}