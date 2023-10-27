package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface OutstandingRentalSheetRepository extends JpaRepository<OutstandingRentalSheet, Long> {
	
	List<OutstandingRentalSheet> findByRentalSheetLeaderId(long leaderId);
	
	List<OutstandingRentalSheet> findByRentalSheetWorkerId(long workerId);
	
	OutstandingRentalSheet findByRentalSheet(RentalSheet RentalSheet);
	
}