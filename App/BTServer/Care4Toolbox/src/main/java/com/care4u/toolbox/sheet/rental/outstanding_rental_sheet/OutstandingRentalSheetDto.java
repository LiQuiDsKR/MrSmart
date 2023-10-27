package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;

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
	
	@Builder
	public OutstandingRentalSheetDto(long id, RentalSheet rentalSheet, int totalCount, int totalOutstandingCount) {
		this.id = id;
		this.rentalSheetDto = new RentalSheetDto(rentalSheet);
		this.totalCount = totalCount;
		this.totalOutstandingCount = totalOutstandingCount;
	}
	
	public OutstandingRentalSheetDto(OutstandingRentalSheet outstandingRentalSheet) {
		this.id = outstandingRentalSheet.getId();
		this.rentalSheetDto = new RentalSheetDto(outstandingRentalSheet.getRentalSheet());
		this.totalCount = outstandingRentalSheet.getTotalCount();
		this.totalOutstandingCount = outstandingRentalSheet.getTotalOutstandingCount();
	}
	
}
