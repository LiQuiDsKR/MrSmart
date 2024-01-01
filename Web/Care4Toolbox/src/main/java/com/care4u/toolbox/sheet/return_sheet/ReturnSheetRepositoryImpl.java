package com.care4u.toolbox.sheet.return_sheet;

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
import com.care4u.toolbox.sheet.return_tool.QReturnTool;
import com.care4u.toolbox.sheet.supply_tool.QSupplyTool;
import com.care4u.toolbox.tool.Tool;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ReturnSheetRepositoryImpl implements ReturnSheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;
	  
	  private static final Logger logger = Logger.getLogger(ReturnSheetRepositoryImpl.class);

	    public ReturnSheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }
	    
	    private BooleanExpression searchPartEquals(Long partId) {
	    	return (partId == null || partId == 0) ? null :
	    		QReturnSheet.returnSheet.leader.part.id.eq(partId)
		    	.or(QReturnSheet.returnSheet.leader.part.subPart != null ?
		    			QReturnSheet.returnSheet.leader.part.subPart.id.eq(partId) 
		    			.or(QReturnSheet.returnSheet.leader.part.subPart.mainPart != null ?
		    					QReturnSheet.returnSheet.leader.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QReturnSheet.returnSheet.worker.part.id.eq(partId))
		    	.or(QReturnSheet.returnSheet.worker.part.subPart != null ?
		    			QReturnSheet.returnSheet.worker.part.subPart.id.eq(partId)
		    			.or(QReturnSheet.returnSheet.worker.part.subPart.mainPart != null ?
		    					QReturnSheet.returnSheet.worker.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QReturnSheet.returnSheet.approver.part.id.eq(partId))
		    	.or(QReturnSheet.returnSheet.approver.part.subPart != null ?
		    			QReturnSheet.returnSheet.approver.part.subPart.id.eq(partId)
		    			.or(QReturnSheet.returnSheet.approver.part.subPart.mainPart != null ?
		    					QReturnSheet.returnSheet.approver.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	;
		}
	    
	    private BooleanExpression searchMembershipEquals(Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover) {
	    	if (membership == null) {
	    		return Expressions.asBoolean(true).isTrue();
	    	}else {
		    	BooleanExpression worker = isWorker? QReturnSheet.returnSheet.worker.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression leader = isLeader? QReturnSheet.returnSheet.leader.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression approver = isApprover? QReturnSheet.returnSheet.approver.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	
		    	return worker.or(leader).or(approver);
	    	}
	    }
	    
	    //supplyTool join하고 사용합시다.
	    private BooleanExpression searchToolEquals(Tool tool) {
	    	return tool == null ? Expressions.asBoolean(true).isTrue() : QReturnTool.returnTool.rentalTool.tool.eq(tool);
	    }

		@Override
		public Page<ReturnSheet> findBySearchQuery(Long partId, Membership membership, Boolean isWorker,
				Boolean isLeader, Boolean isApprover, Tool tool, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			QReturnSheet rSheet = QReturnSheet.returnSheet;
			QReturnTool rTool = QReturnTool.returnTool;
			QSubPart rSubPart = QSubPart.subPart;
			QMainPart rMainPart = QMainPart.mainPart;
			
			List<ReturnSheet> content = queryFactory
                .selectDistinct(rSheet)
                .from(rTool)
                .join(rTool.returnSheet,rSheet)
                .where(
                		rSheet.eventTimestamp.between(startDate, endDate)
                		.and(searchMembershipEquals(membership,isWorker,isLeader,isApprover))
                		.and(searchPartEquals(partId))
                		.and(searchToolEquals(tool))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
