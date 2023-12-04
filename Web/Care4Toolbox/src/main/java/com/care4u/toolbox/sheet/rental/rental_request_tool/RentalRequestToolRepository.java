package com.care4u.toolbox.sheet.rental.rental_request_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface RentalRequestToolRepository extends JpaRepository<RentalRequestTool, Long> {
	
	List<RentalRequestTool> findAllByRentalRequestSheetId(long RentalRequestSheetId);
	
	List<RentalRequestTool> findAllByToolId(long toolId);
}