package com.care4u.toolbox.sheet.buy_tool;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface BuyToolRepository extends JpaRepository<BuyTool, Long> {
	
	List<BuyTool> findAllByBuySheet(RentalSheet RentalSheet);

	List<BuyTool> findAllByBuySheetId(long id);
	
}