package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

public interface StockStatusRepositoryCustom {
	StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryByToolStateDto> getStockStatusSummary(long toolboxId, LocalDate startDate, LocalDate endDate);
	StockStatusSummaryByMainGroupDto getStockStatusSummaryByMainGroupDto(long toolboxId, LocalDate currentDate);
	
}
