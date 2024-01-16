package com.care4u.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int type;
	private Object content;
	
	public Message(int type) {
		this.type = type;
	}
	
	public Message(int type, Object object) {
		this.type = type;
		this.content = object;
	}
}
