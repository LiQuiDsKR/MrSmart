package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

public interface OutstandingRentalSheetRepository extends JpaRepository<OutstandingRentalSheet, Long>, OutstandingRentalSheetRepositoryCustom{
	
	List<OutstandingRentalSheet> findByRentalSheetLeaderId(long leaderId);
	
	List<OutstandingRentalSheet> findByRentalSheetWorkerId(long workerId);
	
	OutstandingRentalSheet findByRentalSheetId(long RentalSheetId);
	
	Page<OutstandingRentalSheet> findByRentalSheetToolboxIdAndRentalSheetEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	List<OutstandingRentalSheet> findByRentalSheetToolboxIdAndRentalSheetEventTimestampBetween(long toolboxId, LocalDateTime startDate, LocalDateTime endDate);
}