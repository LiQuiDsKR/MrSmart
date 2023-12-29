package com.care4u.toolbox.sheet.supply_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface SupplyToolRepository extends JpaRepository<SupplyTool, Long>, SupplyToolRepositoryCustom{
	
	List<SupplyTool> findAllBySupplySheet(RentalSheet RentalSheet);

	List<SupplyTool> findAllBySupplySheetId(long id);
	
}