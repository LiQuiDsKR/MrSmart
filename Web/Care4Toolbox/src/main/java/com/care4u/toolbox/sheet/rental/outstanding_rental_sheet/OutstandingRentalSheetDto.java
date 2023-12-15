package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.util.List;

import com.care4u.constant.OutstandingState;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OutstandingRentalSheetDto {
	
	private long id;
	
	private RentalSheetDto rentalSheetDto;
	
	private int totalCount;
	
	private int totalOutstandingCount;
	
	private OutstandingState outstandingStatus;
	
	@Builder
	public OutstandingRentalSheetDto(long id, RentalSheet rentalSheet, List<RentalToolDto> rentalToolList, int totalCount, int totalOutstandingCount, OutstandingState outstandingStatus) {
		this.id = id;
		this.rentalSheetDto = new RentalSheetDto(rentalSheet,rentalToolList);
		this.totalCount = totalCount;
		this.totalOutstandingCount = totalOutstandingCount;
		this.outstandingStatus = outstandingStatus;
	}
	
	public OutstandingRentalSheetDto(OutstandingRentalSheet outstandingRentalSheet, List<RentalToolDto> rentalToolList) {
		this.id = outstandingRentalSheet.getId();
		this.rentalSheetDto = new RentalSheetDto(outstandingRentalSheet.getRentalSheet(),rentalToolList);
		this.totalCount = outstandingRentalSheet.getTotalCount();
		this.totalOutstandingCount = outstandingRentalSheet.getTotalOutstandingCount();
		this.outstandingStatus = outstandingRentalSheet.getOutstandingStatus();
	}
	
}
