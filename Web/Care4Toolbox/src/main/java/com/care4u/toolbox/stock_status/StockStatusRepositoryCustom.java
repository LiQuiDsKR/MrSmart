package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.part.Part;
import com.care4u.toolbox.tool.Tool;

public interface StockStatusRepositoryCustom {
	StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryByToolStateDto> getStockStatusSummary(Long partId, Membership membership, Tool tool, Long toolboxId, Boolean isWorker, Boolean isLeader, Boolean isApprover, LocalDate startDate, LocalDate endDate);
	List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDto(long toolboxId, LocalDate currentDate);
	List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDtoWithMonth(long toolboxId, LocalDate currentDate);
	Page<StockStatus> findAllByToolboxIdAndCurrentDay(Long toolboxId,LocalDate date,String toolName,List<Long> subGroupIds,Pageable pageable);
}
