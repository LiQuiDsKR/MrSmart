package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class OutstandingRentalSheetRepositoryImpl implements OutstandingRentalSheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;

	    public OutstandingRentalSheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }

		@Override
		public Page<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(
				long membershipId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
					
			QOutstandingRentalSheet outstandingRentalSheet = QOutstandingRentalSheet.outstandingRentalSheet;
			
			BooleanExpression condition = outstandingRentalSheet.rentalSheet.eventTimestamp.between(startDate, endDate)
					.and(outstandingRentalSheet.rentalSheet.worker.id.eq(membershipId)
							.or(outstandingRentalSheet.rentalSheet.leader.id.eq(membershipId))
							.or(outstandingRentalSheet.rentalSheet.approver.id.eq(membershipId))
					);
			
			List<OutstandingRentalSheet> content = queryFactory
					.selectFrom(outstandingRentalSheet)
					.where(condition)
			        .offset(pageable.getOffset())
			        .limit(pageable.getPageSize())
					.fetch();
			
			long total = queryFactory
			        .selectFrom(outstandingRentalSheet)
			        .where(condition)
			        .fetchCount();

		    return new PageImpl<>(content, pageable, total);
		}

		@Override
		public List<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(
				long membershipId, LocalDateTime startDate, LocalDateTime endDate) {
			// TODO Auto-generated method stub
			return null;
		}
}
