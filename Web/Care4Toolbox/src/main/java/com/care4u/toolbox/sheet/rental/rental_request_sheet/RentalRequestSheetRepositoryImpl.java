package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.QMembership;
import com.care4u.toolbox.Toolbox;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class RentalRequestSheetRepositoryImpl implements RentalRequestSheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;
	  
	  private static final Logger logger = Logger.getLogger(RentalRequestSheetRepositoryImpl.class);

	    public RentalRequestSheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
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
		public Page<RentalRequestSheet> findBySearchQuery(SheetState status, Membership membership, Boolean isWorker, Boolean isLeader, Toolbox toolbox, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			
			List<RentalRequestSheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sSheet)
                .where(
                		searchMembershipEquals(membership,isWorker,isLeader)
                		.and(sSheet.toolbox.eq(toolbox))
                		.and(sSheet.status.eq(status))
                		.and(sSheet.eventTimestamp.between(startDate, endDate))
                )
                .orderBy(sSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}

		@Override
		public Page<RentalRequestSheet> findByMembership(SheetState status, Membership membership, Pageable pageable) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			
			List<RentalRequestSheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sSheet)
                .where(
                		searchMembershipEquals(membership,true,true)
                		.and(sSheet.status.eq(status))
                )
                .orderBy(sSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

			long total = queryFactory.select(Wildcard.count).from(sSheet).where(searchMembershipEquals(membership,true,true).and(sSheet.status.eq(status))).fetchOne();
			
	        return new PageImpl<>(content, pageable, total);
		}

		@Override
		public long countByMembership(SheetState status, Membership membership) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			long total = queryFactory.select(Wildcard.count).from(sSheet).where(searchMembershipEquals(membership,true,true).and(sSheet.status.eq(status))).fetchOne();
			return total;
		}

		@Override
		public Page<RentalRequestSheet> findByToolbox(SheetState status, Toolbox toolbox, Pageable pageable) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			
			List<RentalRequestSheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sSheet)
                .where(
                		sSheet.toolbox.eq(toolbox)
                		.and(sSheet.status.eq(status))
                )
                .orderBy(sSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

			long total = queryFactory.select(Wildcard.count).from(sSheet).where(sSheet.toolbox.eq(toolbox).and(sSheet.status.eq(status))).fetchOne();
			
	        return new PageImpl<>(content, pageable, total);
		}

		@Override
		public long countByToolbox(SheetState status, Toolbox toolbox) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			long total = queryFactory.select(Wildcard.count).from(sSheet).where(sSheet.toolbox.eq(toolbox).and(sSheet.status.eq(status))).fetchOne();
			return total;
		}
		
		
}
