package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


/**
 * requestSheet를 승인할 때, approverId를 따로 보내기 어려워서 만든 클래스
 */
@Getter
@ToString
public class RentalRequestSheetWithApproverIdDto {
	@Valid
	private RentalRequestSheetDto rentalRequestSheetDto;
	
	private long approverId;
	
	@Builder
	public RentalRequestSheetWithApproverIdDto(RentalRequestSheetDto rentalRequestSheetDto,long approverId){
		this.approverId=approverId;
		this.rentalRequestSheetDto=rentalRequestSheetDto;
	}
}
