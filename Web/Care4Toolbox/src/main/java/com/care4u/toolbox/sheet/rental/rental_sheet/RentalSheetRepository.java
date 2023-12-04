package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.care4u.hr.membership.Membership;

public interface RentalSheetRepository extends JpaRepository<RentalSheet, Long> {
	
	Page<RentalSheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByWorkerIdAndEventTimestampBetween(long workerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalSheet> findAllByLeaderIdAndEventTimestampBetween(long leaderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	@Query("SELECT r FROM RentalSheet r "
			+ "WHERE (r.worker = :member OR r.leader = :member OR r.approver = :member) "
			+ "AND r.eventTimestamp BETWEEN :startDate AND :endDate ORDER BY r.eventTimestamp DESC")
	Page<RentalSheet> findByMemberAndEventTimestampBetween(
			@Param("member") Membership member,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			Pageable pageable
			);
}