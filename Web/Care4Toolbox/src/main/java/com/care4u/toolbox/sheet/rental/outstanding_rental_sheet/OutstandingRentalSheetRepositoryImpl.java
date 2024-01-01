package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.QRentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
			
			QOutstandingRentalSheet outstandingRentalSheet = QOutstandingRentalSheet.outstandingRentalSheet;
			
			BooleanExpression condition = outstandingRentalSheet.rentalSheet.eventTimestamp.between(startDate, endDate)
					.and(outstandingRentalSheet.rentalSheet.worker.id.eq(membershipId)
							.or(outstandingRentalSheet.rentalSheet.leader.id.eq(membershipId))
							.or(outstandingRentalSheet.rentalSheet.approver.id.eq(membershipId))
					);
			
			List<OutstandingRentalSheet> content = queryFactory
					.selectFrom(outstandingRentalSheet)
					.where(condition)
					.fetch();

		    return content;
		}

		@Override
		public List<OutstandingRentalSheet> findByOutstandingStatusAndLeaderIdOrWorkerIdOrApproverId(
				OutstandingState status, long membershipId) {
			QOutstandingRentalSheet outstandingRentalSheet = QOutstandingRentalSheet.outstandingRentalSheet;
			
			BooleanExpression condition = outstandingRentalSheet.outstandingStatus.eq(status)
					.and(outstandingRentalSheet.rentalSheet.worker.id.eq(membershipId)
							.or(outstandingRentalSheet.rentalSheet.leader.id.eq(membershipId))
							.or(outstandingRentalSheet.rentalSheet.approver.id.eq(membershipId))
					);
			
			List<OutstandingRentalSheet> content = queryFactory
					.selectFrom(outstandingRentalSheet)
					.where(condition)
					.fetch();
			return content;
		}
		
		
		
		private BooleanExpression searchMembershipEquals(Membership membership, Boolean isWorker, Boolean isLeader) {
	    	if (membership == null) {
	    		return Expressions.asBoolean(true).isTrue();
	    	}else {
		    	BooleanExpression worker = isWorker? QRentalRequestSheet.rentalRequestSheet.worker.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression leader = isLeader? QRentalRequestSheet.rentalRequestSheet.leader.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	
		    	return worker.or(leader);
	    	}
	    }
	    
	    
		@Override
		public Page<OutstandingRentalSheet> findBySearchQuery(OutstandingState status, Membership membership,
				Boolean isWorker, Boolean isLeader, Toolbox toolbox, LocalDateTime startDate,
				LocalDateTime endDate, Pageable pageable) {
			QOutstandingRentalSheet sSheet = QOutstandingRentalSheet.outstandingRentalSheet;
			
			List<OutstandingRentalSheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sSheet)
                .where(
                		(searchMembershipEquals(membership,isWorker,isLeader)
                		.and(sSheet.rentalSheet.toolbox.eq(toolbox)))
                		.and(sSheet.outstandingStatus.eq(status))
                		.and(sSheet.rentalSheet.eventTimestamp.between(startDate, endDate))
                )
                .orderBy(sSheet.rentalSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
