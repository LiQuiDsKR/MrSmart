package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRequestSheetRepository extends JpaRepository<RentalRequestSheet, Long> {
	
	Page<RentalRequestSheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	@Query(value = "SELECT * FROM RentalRequestSheet WHERE (worker_id = :id1 OR leader_id = :id2) " +
            "AND event_timestamp BETWEEN :startDate AND :endDate", nativeQuery = true)
	Page<RentalRequestSheet> findAllByWorkerIdOrLeaderIdAndEventTimestampBetween(@Param("id1") long workerId, @Param("id2") long leaderId, 
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
	
}