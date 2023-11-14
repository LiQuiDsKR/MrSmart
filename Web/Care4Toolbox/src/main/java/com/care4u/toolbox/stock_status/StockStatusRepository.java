package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockStatusRepository extends JpaRepository<StockStatus, Long> {
	
	List<StockStatus> findAllByToolboxIdAndCurrentDayBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate);
	
	StockStatus findByToolIdAndToolboxIdAndCurrentDayBetween(long toolId, long toolboxId, LocalDateTime startDate, LocalDateTime endDate);
	
}