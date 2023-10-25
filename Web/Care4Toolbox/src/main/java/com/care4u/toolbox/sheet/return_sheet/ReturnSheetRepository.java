package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnSheetRepository extends JpaRepository<ReturnSheet, Long> {
	
	Page<ReturnSheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByWorkerIdAndEventTimestampBetween(long workerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByLeaderIdAndEventTimestampBetween(long leaderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
}