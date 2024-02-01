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
	
	Page<RentalRequestSheet> findAllByStatusAndToolboxIdAndEventTimestampBetweenOrderByEventTimestampAsc(SheetState status, long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByToolboxIdAndEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	
	Page<RentalRequestSheet> findAllByToolboxId(long toolboxId, Pageable pageable);
	
	List<RentalRequestSheet> findAllByStatusAndToolboxIdOrderByEventTimestampAsc(SheetState status, long toolboxId);
	
	RentalRequestSheet findByEventTimestamp(LocalDateTime eventTimestamp);	
}