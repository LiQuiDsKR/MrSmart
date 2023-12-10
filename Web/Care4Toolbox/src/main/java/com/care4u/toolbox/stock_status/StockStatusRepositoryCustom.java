package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

public interface StockStatusRepositoryCustom {
	StockStatusSummaryDto getStockStatusSummary(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryDto> getStockStatusSummary(long toolboxId, LocalDate startDate, LocalDate endDate);
}
