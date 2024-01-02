package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.tool.Tool;

public interface RentalRequestSheetRepositoryCustom {
	Page<RentalRequestSheet> findBySearchQuery(SheetState status, Membership membership, Boolean isWorker, Boolean isLeader, Toolbox toolbox, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
	Page<RentalRequestSheet> findByMembership(SheetState status, Membership membership, Pageable pageable);
	long countByMembership(SheetState status, Membership membership);
}
