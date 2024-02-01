package com.care4u.toolbox.sheet.supply_tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.tool.Tool;

public interface SupplyToolRepositoryCustom {
	Page<SupplyTool> findBySearchQuery(Long partId, Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover, Tool tool, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
