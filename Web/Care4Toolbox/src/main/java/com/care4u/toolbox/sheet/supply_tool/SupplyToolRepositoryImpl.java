package com.care4u.toolbox.sheet.supply_tool;

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
import com.care4u.toolbox.sheet.supply_sheet.QSupplySheet;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheet;
import com.care4u.toolbox.sheet.supply_tool.QSupplyTool;
import com.care4u.toolbox.tool.Tool;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class SupplyToolRepositoryImpl implements SupplyToolRepositoryCustom {
	  private final JPAQueryFactory queryFactory;
	  
	  private static final Logger logger = Logger.getLogger(SupplyToolRepositoryImpl.class);

	    public SupplyToolRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }
	    private BooleanExpression searchPartEquals(Long partId) {
	    	return (partId == null || partId == 0) ? null :
	    		QSupplySheet.supplySheet.leader.part.id.eq(partId)
		    	.or(QSupplySheet.supplySheet.leader.part.subPart != null ?
		    			QSupplySheet.supplySheet.leader.part.subPart.id.eq(partId) 
		    			.or(QSupplySheet.supplySheet.leader.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.leader.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QSupplySheet.supplySheet.worker.part.id.eq(partId))
		    	.or(QSupplySheet.supplySheet.worker.part.subPart != null ?
		    			QSupplySheet.supplySheet.worker.part.subPart.id.eq(partId)
		    			.or(QSupplySheet.supplySheet.worker.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.worker.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	.or(QSupplySheet.supplySheet.approver.part.id.eq(partId))
		    	.or(QSupplySheet.supplySheet.approver.part.subPart != null ?
		    			QSupplySheet.supplySheet.approver.part.subPart.id.eq(partId)
		    			.or(QSupplySheet.supplySheet.approver.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.approver.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(false).isTrue())
		    			: Expressions.asBoolean(false).isTrue())
		    	;
		}
	    
	    private BooleanExpression searchMembershipEquals(Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover) {
	    	if (membership == null) {
	    		return Expressions.asBoolean(true).isTrue();
	    	}else {
		    	BooleanExpression worker = isWorker? QSupplySheet.supplySheet.worker.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression leader = isLeader? QSupplySheet.supplySheet.leader.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	BooleanExpression approver = isApprover? QSupplySheet.supplySheet.approver.eq(membership) : Expressions.asBoolean(false).isTrue();
		    	
		    	return worker.or(leader).or(approver);
	    	}
	    }
	    
	    //supplyTool join하고 사용합시다.
	    private BooleanExpression searchToolEquals(Tool tool) {
	    	return tool == null ? Expressions.asBoolean(true).isTrue() : QSupplyTool.supplyTool.tool.eq(tool);
	    }

		@Override
		public Page<SupplyTool> findBySearchQuery(Long partId, Membership membership, Boolean isWorker,
				Boolean isLeader, Boolean isApprover, Tool tool, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			QSupplySheet sSheet = QSupplySheet.supplySheet;
			QSupplyTool sTool = QSupplyTool.supplyTool;
			QSubPart sSubPart = QSubPart.subPart;
			QMainPart sMainPart = QMainPart.mainPart;
			
			List<SupplyTool> content = queryFactory
				.select(sTool)
                .from(sTool)
                .join(sTool.supplySheet,sSheet)
                .where(
                		sSheet.eventTimestamp.between(startDate, endDate)
                		.and(searchMembershipEquals(membership,isWorker,isLeader,isApprover))
                		.and(searchPartEquals(partId))
                		.and(searchToolEquals(tool))
                )
                .orderBy(sTool.replacementDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
