package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;

public interface RentalRequestSheetRepository extends JpaRepository<RentalRequestSheet, Long>, RentalRequestSheetRepositoryCustom{
	
	Page<RentalRequestSheet> findAllByStatusAndToolboxIdOrderByEventTimestampAsc(SheetState status, long toolboxId, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByStatusAndToolboxIdAndEventTimestampBetweenOrderByEventTimestampAsc(SheetState status, long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByToolboxId(long toolboxId, Pageable pageable);
	
	List<RentalRequestSheet> findAllByStatusAndToolboxIdOrderByEventTimestampAsc(SheetState status, long toolboxId);
	
	@Query("SELECT r FROM RentalRequestSheet r " +
	        "WHERE (r.worker.id = :id1 OR r.leader.id = :id2) " +
	        "AND r.eventTimestamp BETWEEN :startDate AND :endDate")
	Page<RentalRequestSheet> findAllByWorkerIdOrLeaderIdAndEventTimestampBetween(
	        @Param("id1") long workerId, @Param("id2") long leaderId,
	        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

	RentalRequestSheet findByEventTimestamp(LocalDateTime eventTimestamp);	
}