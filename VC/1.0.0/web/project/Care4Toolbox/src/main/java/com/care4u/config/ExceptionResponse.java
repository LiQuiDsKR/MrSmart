package com.care4u.config;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

	private int httpStatusValue;
	private Date timestamp;
	private String message;
	private String details;
	
	public ExceptionResponse(int httpStatusValue, String message, String details) {
		this.timestamp = new Date();
		this.httpStatusValue = httpStatusValue;
		this.message = message;
		this.details = details;
	}
}