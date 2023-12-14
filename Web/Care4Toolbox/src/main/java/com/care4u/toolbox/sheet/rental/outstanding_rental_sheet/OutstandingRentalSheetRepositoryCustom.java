package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutstandingRentalSheetRepositoryCustom {
	Page<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	List<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(long membershipId, LocalDateTime startDate, LocalDateTime endDate);
}
