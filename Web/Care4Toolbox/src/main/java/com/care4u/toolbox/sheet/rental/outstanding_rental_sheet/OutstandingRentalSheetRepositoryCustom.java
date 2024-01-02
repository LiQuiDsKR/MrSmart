package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;

public interface OutstandingRentalSheetRepositoryCustom {
	Page<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	List<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate);
	List<OutstandingRentalSheet> findByOutstandingStatusAndLeaderIdOrWorkerIdOrApproverId(OutstandingState status, long membershipId);
	Page<OutstandingRentalSheet> findBySearchQuery(OutstandingState status, Membership membership, Boolean isWorker,
			Boolean isLeader, Toolbox toolbox, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	Page<OutstandingRentalSheet> findByMembership(Membership membership, Pageable pageable);
	Long countByMembership(Membership membership);
	Page<OutstandingRentalSheet> findByToolbox(OutstandingState status, Toolbox toolbox, Pageable pageable);
	Long countByToolbox(OutstandingState status,Toolbox toolbox);
}
