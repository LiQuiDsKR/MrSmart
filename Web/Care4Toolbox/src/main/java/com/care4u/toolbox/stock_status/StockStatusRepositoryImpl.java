package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class StockStatusRepositoryImpl implements StockStatusRepositoryCustom {
	  private final JPAQueryFactory queryFactory;

	    public StockStatusRepositoryImpl(EntityManager entityManager) {
	        this.queryFactory = new JPAQueryFactory(entityManager);
	    }

	    @Override
		public List<StockStatusSummaryByToolStateDto> getStockStatusSummary(long toolboxId, LocalDate startDate, LocalDate endDate) {
	    	QStockStatus stockStatus = QStockStatus.stockStatus;

	        return queryFactory
	            .select(
	                Projections.constructor(
	                    StockStatusSummaryByToolStateDto.class,
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
	    public StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate) {
	        QStockStatus stockStatus = QStockStatus.stockStatus;

	        return queryFactory
	            .select(
	                Projections.constructor(
	                    StockStatusSummaryByToolStateDto.class,
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
		public StockStatusSummaryByMainGroupDto getStockStatusSummaryByMainGroupDto(long toolboxId,
				LocalDate currentDate) {
			// TODO Auto-generated method stub
			return null;
		}
}
