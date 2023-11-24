package com.care4u.toolbox.sheet.rental.rental_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface RentalToolRepository extends JpaRepository<RentalTool, Long> {
	
	List<RentalTool> findAllByRentalSheetId(long rentalSheetId);
	
}