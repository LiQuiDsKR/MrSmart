package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDate;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalSheetSearchDto {
	
	private long membershipId;
	
	private int page;
	
	private int size;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
}
