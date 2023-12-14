package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class OutstandingRentalSheetRepositoryImpl implements OutstandingRentalSheetRepositoryCustom {
	  private final JPAQueryFactory queryFactory;

	    public OutstandingRentalSheetRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }

	    
	    
	    @Override
		public List<StockStatusSummaryDto> getStockStatusSummary(long toolboxId, LocalDate startDate, LocalDate endDate) {
	    	QStockStatus stockStatus = QStockStatus.stockStatus;

	        return queryFactory
	            .select(
	                Projections.constructor(
	                    StockStatusSummaryDto.class,
	                    stockStatus.toolbox.name,
	                    stockStatus.currentDay,
	                    stockStatus.totalCount.sum(),
	                    stockStatus.rentalCount.sum(),
	                    stockStatus.buyCount.sum(),
	                    stockStatus.goodCount.sum(),
	                    stockStatus.faultCount.sum(),
	                    stockStatus.damageCount.sum(),
	                    stockStatus.lossCount.sum(),
	                    stockStatus.discardCount.sum()
	                )
	            )
	            .from(stockStatus)
	            .where(
	                stockStatus.toolbox.id.eq(toolboxId)
	                .and(stockStatus.currentDay.between(startDate,endDate))
	            )
	            .groupBy(stockStatus.currentDay)
	            .fetch();
	    }

		@Override
	    public StockStatusSummaryDto getStockStatusSummary(long toolboxId, LocalDate currentDate) {
	        QStockStatus stockStatus = QStockStatus.stockStatus;

	        return queryFactory
	            .select(
	                Projections.constructor(
	                    StockStatusSummaryDto.class,
	                    stockStatus.toolbox.name,
	                    stockStatus.currentDay,
	                    stockStatus.totalCount.sum(),
	                    stockStatus.rentalCount.sum(),
	                    stockStatus.buyCount.sum(),
	                    stockStatus.goodCount.sum(),
	                    stockStatus.faultCount.sum(),
	                    stockStatus.damageCount.sum(),
	                    stockStatus.lossCount.sum(),
	                    stockStatus.discardCount.sum()
	                )
	            )
	            .from(stockStatus)
	            .where(
	                stockStatus.toolbox.id.eq(toolboxId)
	                .and(stockStatus.currentDay.eq(currentDate))
	            )
	            .fetchOne();
	    }



		@Override
		public Page<OutstandingRentalSheet> findByRentalSheetMembershipIdAndRentalSheetEventTimestampBetween(
				long membershipId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
			// QueryDSL 엔티티 인스턴스화
			QOutstandingRentalSheet outstandingRentalSheet = QOutstandingRentalSheet.outstandingRentalSheet;

			// 주어진 membershipId와 일치하는 workerId, approverId, 또는 leaderId를 찾는 조건
			Long membershipId = // membershipId 설정
			BooleanExpression condition = outstandingRentalSheet.workerId.eq(membershipId)
			    .or(outstandingRentalSheet.approverId.eq(membershipId))
			    .or(outstandingRentalSheet.leaderId.eq(membershipId));

			// QueryDSL 쿼리 구성 및 실행
			List<OutstandingRentalSheet> results = queryFactory
			    .selectFrom(outstandingRentalSheet)
			    .where(condition)
			    .fetch();

			return null;
		}
}
