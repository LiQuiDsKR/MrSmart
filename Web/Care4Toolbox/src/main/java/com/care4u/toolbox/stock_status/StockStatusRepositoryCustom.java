package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface StockStatusRepositoryCustom {
	StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryByToolStateDto> getStockStatusSummary(long toolboxId, LocalDate startDate, LocalDate endDate);
	List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDto(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDtoWithMonth(long toolboxId, LocalDate currentDate);
	Page<StockStatus> findAllByToolboxIdAndCurrentDay(Long toolboxId,LocalDate date,String toolName,List<Long> subGroupIds,Pageable pageable);
}
