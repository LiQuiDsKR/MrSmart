package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalSheetRepository extends JpaRepository<RentalSheet, Long> {
	
	Page<RentalSheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByWorkerIdAndEventTimestampBetween(long workerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByLeaderIdAndEventTimestampBetween(long leaderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
}