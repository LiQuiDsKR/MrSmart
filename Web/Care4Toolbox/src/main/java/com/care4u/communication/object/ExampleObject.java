package com.care4u.communication.object;

import java.io.Serializable;

public class ExampleObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String data;
	
	public ExampleObject(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
}
