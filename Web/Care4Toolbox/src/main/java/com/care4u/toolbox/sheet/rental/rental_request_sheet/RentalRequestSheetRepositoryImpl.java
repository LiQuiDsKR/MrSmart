package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
		public Page<RentalRequestSheet> findBySearchQuery(SheetState status, Membership membership, Boolean isWorker, Boolean isLeader, Toolbox toolbox, Pageable pageable) {
			QRentalRequestSheet sSheet = QRentalRequestSheet.rentalRequestSheet;
			
			List<RentalRequestSheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sSheet)
                .where(
                		searchMembershipEquals(membership,isWorker,isLeader)
                		.and(sSheet.toolbox.eq(toolbox))
                		.and(sSheet.status.eq(status))
                )
                .orderBy(sSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
