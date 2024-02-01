package com.care4u.toolbox.sheet.supply_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.group.sub_group.SubGroup;
import com.care4u.toolbox.tool.Tool;

public interface SupplySheetRepositoryCustom {
	Page<SupplySheet> findBySearchQuery(Long partId, Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover, Tool tool, SubGroup subGroup, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
