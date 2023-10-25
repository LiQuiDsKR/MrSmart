package com.care4u.toolbox.sheet.return_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnToolRepository extends JpaRepository<ReturnTool, Long> {
	
	List<ReturnTool> findAllByReturnSheetId(long returnSheetId);
	
	List<ReturnTool> findAllByRentalToolId(long rentalToolId);
	
}