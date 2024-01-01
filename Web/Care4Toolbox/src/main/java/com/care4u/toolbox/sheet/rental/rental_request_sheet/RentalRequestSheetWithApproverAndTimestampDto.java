package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;

import javax.validation.Valid;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


/**
 * requestSheet 보류 승인
 */
@Getter
@ToString
public class RentalRequestSheetWithApproverAndTimestampDto {
	@Valid
	private RentalRequestSheetWithApproverIdDto sheet;
	@NotNull
	private String timestamp;
	
	@Builder
	public RentalRequestSheetWithApproverAndTimestampDto(RentalRequestSheetWithApproverIdDto sheet,String timestamp){
		this.sheet =sheet;
		this.timestamp=timestamp;
	}
}
