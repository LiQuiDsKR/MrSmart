package com.care4u.toolbox.sheet.buy_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.care4u.hr.membership.Membership;

public interface BuySheetRepository extends JpaRepository<BuySheet, Long> {
	
	Page<BuySheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<BuySheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	Page<BuySheet> findByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate,Pageable pageable);

	Page<BuySheet> findByToolboxIdAndEventTimestampBetweenOrderByEventTimestampDesc(long toolboxId, LocalDateTime of, LocalDateTime of2, Pageable pageable);

	
}