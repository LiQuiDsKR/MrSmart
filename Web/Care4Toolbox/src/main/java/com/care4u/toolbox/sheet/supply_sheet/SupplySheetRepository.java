package com.care4u.toolbox.sheet.supply_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;

public interface SupplySheetRepository extends JpaRepository<SupplySheet, Long> , SupplySheetRepositoryCustom{
	
	Page<SupplySheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<SupplySheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<SupplySheet> findAllByWorkerIdAndEventTimestampBetween(long workerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<SupplySheet> findAllByLeaderIdAndEventTimestampBetween(long leaderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}