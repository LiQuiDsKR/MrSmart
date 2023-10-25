package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockStatusRepository extends JpaRepository<StockStatus, Long> {
	
	List<StockStatus> findAllByToolboxIdAndCurrentDay(long toolboxId, LocalDate currentDay);
	
}