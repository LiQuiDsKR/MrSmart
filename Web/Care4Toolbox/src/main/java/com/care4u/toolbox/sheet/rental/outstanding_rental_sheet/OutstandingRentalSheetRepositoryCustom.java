package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;

public interface OutstandingRentalSheetRepositoryCustom {
	Page<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	List<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate);
	List<OutstandingRentalSheet> findByOutstandingStatusAndLeaderIdOrWorkerIdOrApproverId(OutstandingState status, long membershipId);
	Page<OutstandingRentalSheet> findBySearchQuery(OutstandingState status, long membershipId, Boolean isWorker,
			Boolean isLeader, long toolboxId, LocalDate startLocalDate, LocalDate endLocalDate, Pageable pageable);
}
