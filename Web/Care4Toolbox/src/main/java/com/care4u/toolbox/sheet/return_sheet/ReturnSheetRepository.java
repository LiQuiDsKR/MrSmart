package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface ReturnSheetRepository extends JpaRepository<ReturnSheet, Long> , ReturnSheetRepositoryCustom{
	
	Page<ReturnSheet> findAllByEventTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByWorkerIdAndEventTimestampBetween(long workerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<ReturnSheet> findAllByLeaderIdAndEventTimestampBetween(long leaderId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	@Query("SELECT r FROM ReturnSheet r "
			+ "WHERE (r.worker = :member OR r.leader = :member OR r.approver = :member) "
			+ "AND r.eventTimestamp BETWEEN :startDate AND :endDate ORDER BY r.eventTimestamp DESC")
	Page<ReturnSheet> findByMemberAndEventTimestampBetween(
			@Param("member") Membership member,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			Pageable pageable
			);

	ReturnSheet findByEventTimestamp(LocalDateTime eventTimestamp);
}