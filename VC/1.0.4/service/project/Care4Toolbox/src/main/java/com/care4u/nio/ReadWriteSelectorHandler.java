package com.care4u.nio;

public interface ReadWriteSelectorHandler extends SelectorHandler {  
	  public void handleRead();
	  public void handleWrite();    
	  public void closeChannel() throws Exception;
	}