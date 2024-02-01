package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.QMainPart;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipRestController;
import com.care4u.hr.membership.QMembership;
import com.care4u.hr.part.QPart;
import com.care4u.hr.sub_part.QSubPart;
import com.care4u.toolbox.sheet.rental.rental_tool.QRentalTool;
import com.care4u.toolbox.sheet.supply_tool.QSupplyTool;
import com.care4u.toolbox.tool.Tool;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class RentalSheetRepositoryImpl implements RentalSheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;
	  
	  private static final Logger logger = Logger.getLogger(RentalSheetRepositoryImpl.class);

	    public RentalSheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }
	    
	    private BooleanExpression searchPartEquals(Long partId) {
	    	return (partId == null || partId == 0) ? null :
	    		QRentalSheet.rentalSheet.leader.part.id.eq(partId)
		    	.or(QRentalSheet.rentalSheet.leader.part.subPart != null ?
		    			QRentalSheet.rentalSheet.leader.part.subPart.id.eq(partId) 
		    			.or(QRentalSheet.rentalSheet.leader.part.subPart.mainPart != null ?
		    					QRentalSheet.rentalSheet.leader.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QRentalSheet.rentalSheet.worker.part.id.eq(partId))
		    	.or(QRentalSheet.rentalSheet.worker.part.subPart != null ?
		    			QRentalSheet.rentalSheet.worker.part.subPart.id.eq(partId)
		    			.or(QRentalSheet.rentalSheet.worker.part.subPart.mainPart != null ?
		    					QRentalSheet.rentalSheet.worker.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QRentalSheet.rentalSheet.approver.part.id.eq(partId))
		    	.or(QRentalSheet.rentalSheet.approver.part.subPart != null ?
		    			QRentalSheet.rentalSheet.approver.part.subPart.id.eq(partId)
		    			.or(QRentalSheet.rentalSheet.approver.part.subPart.mainPart != null ?
		    					QRentalSheet.rentalSheet.approver.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	;
		}
	    
	    private BooleanExpression searchMembershipEquals(Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover) {
	    	if (membership == null) {
	    		return Expressions.asBoolean(true).isTrue();
	    	}else {
		    	BooleanExpression worker = isWorker? QRentalSheet.rentalSheet.worker.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression leader = isLeader? QRentalSheet.rentalSheet.leader.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression approver = isApprover? QRentalSheet.rentalSheet.approver.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	
		    	return worker.or(leader).or(approver);
	    	}
	    }
	    
	    //supplyTool join하고 사용합시다.
	    private BooleanExpression searchToolEquals(Tool tool) {
	    	return tool == null ? Expressions.asBoolean(true).isTrue() : QRentalTool.rentalTool.tool.eq(tool);
	    }

		@Override
		public Page<RentalSheet> findBySearchQuery(Long partId, Membership membership, Boolean isWorker,
				Boolean isLeader, Boolean isApprover, Tool tool, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			QRentalSheet rSheet = QRentalSheet.rentalSheet;
			QRentalTool rTool = QRentalTool.rentalTool;
			QSubPart rSubPart = QSubPart.subPart;
			QMainPart rMainPart = QMainPart.mainPart;
			
			List<RentalSheet> content = queryFactory
                .selectDistinct(rSheet)
                .from(rTool)
                .join(rTool.rentalSheet,rSheet)
                .where(
                		rSheet.eventTimestamp.between(startDate, endDate)
                		.and(searchMembershipEquals(membership,isWorker,isLeader,isApprover))
                		.and(searchPartEquals(partId))
                		.and(searchToolEquals(tool))
                )
                .orderBy(rSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
