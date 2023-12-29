package com.care4u.toolbox.sheet.supply_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.constant.OutstandingState;
import com.care4u.constant.Role;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.QMembership;
import com.care4u.toolbox.sheet.supply_tool.QSupplyTool;
import com.care4u.toolbox.tool.Tool;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class SupplySheetRepositoryImpl implements SupplySheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;

	    public SupplySheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }

	    /**
	     * @deprecated
	     */
	    private BooleanExpression searchRoleEquals(Role searchRole){
	        return searchRole == null ? null : QMembership.membership.role.eq(searchRole);
	    }
	    
	    private BooleanExpression searchPartEquals(Long partId) {
	    	return (partId == null || partId == 0) ? null :
	    		QSupplySheet.supplySheet.leader.part.id.eq(partId)
		    	.or(QSupplySheet.supplySheet.leader.part.subPart != null ?
		    			QSupplySheet.supplySheet.leader.part.subPart.id.eq(partId) 
		    			.or(QSupplySheet.supplySheet.leader.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.leader.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(true).isTrue())
		    			: Expressions.asBoolean(true).isTrue())
		    	.or(QSupplySheet.supplySheet.worker.part.id.eq(partId))
		    	.or(QSupplySheet.supplySheet.worker.part.subPart != null ?
		    			QSupplySheet.supplySheet.worker.part.subPart.id.eq(partId)
		    			.or(QSupplySheet.supplySheet.worker.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.worker.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(true).isTrue())
		    			: Expressions.asBoolean(true).isTrue())
		    	.or(QSupplySheet.supplySheet.approver.part.id.eq(partId))
		    	.or(QSupplySheet.supplySheet.approver.part.subPart != null ?
		    			QSupplySheet.supplySheet.approver.part.subPart.id.eq(partId)
		    			.or(QSupplySheet.supplySheet.approver.part.subPart.mainPart != null ?
		    					QSupplySheet.supplySheet.approver.part.subPart.mainPart.id.eq(partId)
		    					: Expressions.asBoolean(true).isTrue())
		    			: Expressions.asBoolean(true).isTrue())
		    	;
		}
	    
	    private BooleanExpression searchWorkerEquals(Membership membership) {
	    	return membership == null? null :
	    		QSupplySheet.supplySheet.worker.eq(membership);
	    }
	    
	    private BooleanExpression searchLeaderEquals(Membership membership) {
	    	return membership == null? null :
	    		QSupplySheet.supplySheet.leader.eq(membership);
	    }
	    
	    private BooleanExpression searchApproverEquals(Membership membership) {
	    	return membership == null? null :
	    		QSupplySheet.supplySheet.approver.eq(membership);
	    }
	    
	    private BooleanExpression searchMembershipEquals(Membership membership, Boolean isWorker, Boolean isLeader, Boolean isApprover) {
	    	if (membership == null) {
	    		return Expressions.asBoolean(true).isTrue();
	    	}else {
		    	BooleanExpression worker = isWorker? searchWorkerEquals(membership) : Expressions.asBoolean(true).isTrue();
		    	BooleanExpression leader = isLeader? searchLeaderEquals(membership) : Expressions.asBoolean(true).isTrue();
		    	BooleanExpression approver = isApprover? searchApproverEquals(membership) : Expressions.asBoolean(true).isTrue();
		    	
		    	return worker.or(leader).or(approver);
	    	}
	    }
	    
	    //supplyTool join하고 사용합시다.
	    private BooleanExpression searchToolEquals(Tool tool) {
	    	return tool == null ? Expressions.asBoolean(true).isTrue() : QSupplyTool.supplyTool.tool.eq(tool);
	    }

		@Override
		public Page<SupplySheet> findBySearchQuery(Long partId, Membership membership, Boolean isWorker,
				Boolean isLeader, Boolean isApprover, Tool tool, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			QSupplySheet sSheet = QSupplySheet.supplySheet;
			QSupplyTool sTool = QSupplyTool.supplyTool;
			
			List<SupplySheet> content = queryFactory
                .selectDistinct(sSheet)
                .from(sTool)
                .join(sTool.supplySheet,sSheet)
                .where(
                		sSheet.eventTimestamp.between(startDate, endDate)
                		.and(searchMembershipEquals(membership,isWorker,isLeader,isApprover))
                		.and(searchPartEquals(partId))
                		.and(searchToolEquals(tool))
                )
                .orderBy(sTool.replacementDate.asc(),sSheet.eventTimestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

	        return new PageImpl<>(content, pageable, content.size());
		}
}
