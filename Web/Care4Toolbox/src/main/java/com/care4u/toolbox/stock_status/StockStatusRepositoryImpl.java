package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.care4u.toolbox.group.main_group.QMainGroup;
import com.care4u.toolbox.group.sub_group.QSubGroup;
import com.care4u.toolbox.sheet.rental.rental_sheet.QRentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.QRentalTool;
import com.care4u.toolbox.sheet.return_sheet.QReturnSheet;
import com.care4u.toolbox.sheet.return_tool.QReturnTool;
import com.care4u.toolbox.tool.QTool;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class StockStatusRepositoryImpl implements StockStatusRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public StockStatusRepositoryImpl(EntityManager entityManager) {
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
	
	@Override
	public List<StockStatusSummaryByToolStateDto> getStockStatusSummary(long toolboxId, LocalDate startDate,
			LocalDate endDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;
		QRentalSheet rentalSheet = QRentalSheet.rentalSheet;
		QRentalTool rentalTool = QRentalTool.rentalTool;
		QReturnSheet returnSheet = QReturnSheet.returnSheet;
		QReturnTool returnTool = QReturnTool.returnTool;
		
		long membershipId=0;
		
		Integer rentalCount = queryFactory
			    .select(rentalTool.count.sum())
			    .from(rentalSheet)
			    .join(rentalTool).on(rentalSheet.id.eq(rentalTool.rentalSheet.id))
			    .where(rentalSheet.worker.id.eq(membershipId))
			    .fetchOne();

		Integer returnCount = queryFactory
			    .select(returnTool.count.sum())
			    .from(returnSheet)
			    .join(returnTool).on(returnSheet.id.eq(returnTool.returnSheet.id))
			    .where(returnSheet.worker.id.eq(membershipId))
			    .fetchOne();


		return queryFactory
				.select(Projections.constructor(StockStatusSummaryByToolStateDto.class, stockStatus.toolbox.name,
						stockStatus.currentDay, stockStatus.totalCount.sum(), stockStatus.rentalCount.sum(),
						stockStatus.buyCount.sum(), stockStatus.goodCount.sum(), stockStatus.faultCount.sum(),
						stockStatus.damageCount.sum(), stockStatus.lossCount.sum(), stockStatus.discardCount.sum(),
						stockStatus.supplyCount.sum()))
				.from(stockStatus)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.between(startDate, endDate)))
				.groupBy(stockStatus.currentDay).fetch();
	}

	@Override
	public StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;

		return queryFactory
				.select(Projections.constructor(StockStatusSummaryByToolStateDto.class, stockStatus.toolbox.name,
						stockStatus.currentDay, stockStatus.totalCount.sum(), stockStatus.rentalCount.sum(),
						stockStatus.buyCount.sum(), stockStatus.goodCount.sum(), stockStatus.faultCount.sum(),
						stockStatus.damageCount.sum(), stockStatus.lossCount.sum(), stockStatus.discardCount.sum(),
						stockStatus.supplyCount.sum()))
				.from(stockStatus)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.eq(currentDate))).fetchOne();
	}

	@Override
	public List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDto(long toolboxId,
			LocalDate currentDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;
		QTool tool = QTool.tool;
		QSubGroup subGroup = QSubGroup.subGroup;
		QMainGroup mainGroup = QMainGroup.mainGroup;

		List<Tuple> results = queryFactory
				.select(stockStatus.toolbox.name, stockStatus.currentDay, mainGroup.name, stockStatus.totalCount.sum(),
						stockStatus.rentalCount.sum(), stockStatus.goodCount.sum(),
						stockStatus.damageCount.sum().add(stockStatus.faultCount.sum()))
				.from(stockStatus).leftJoin(stockStatus.tool, tool).leftJoin(tool.subGroup, subGroup)
				.leftJoin(subGroup.mainGroup, mainGroup)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.eq(currentDate)))
				.groupBy(mainGroup.id, mainGroup.name).fetch();

		List<StockStatusSummaryByMainGroupDto> dtoList = results.stream()
				.map(tuple -> new StockStatusSummaryByMainGroupDto(tuple.get(stockStatus.toolbox.name),
						tuple.get(stockStatus.currentDay), tuple.get(mainGroup.name),
						tuple.get(stockStatus.totalCount.sum()), tuple.get(stockStatus.rentalCount.sum()),
						tuple.get(stockStatus.goodCount.sum()),
						tuple.get(stockStatus.damageCount.sum().add(stockStatus.faultCount.sum()))))
				.collect(Collectors.toList());

		return dtoList;
	}

	@Override
	public List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDtoWithMonth(long toolboxId,
			LocalDate currentDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;
		QTool tool = QTool.tool;
		QSubGroup subGroup = QSubGroup.subGroup;
		QMainGroup mainGroup = QMainGroup.mainGroup;

		// 서브쿼리 정의

		// 메인 쿼리 정의
		List<Tuple> results = queryFactory
				.select(mainGroup.id, mainGroup.name, stockStatus.currentDay.month(), stockStatus.totalCount.sum())
				.from(stockStatus)
				.where(stockStatus.currentDay.in(JPAExpressions.select(stockStatus).from(stockStatus)
						.leftJoin(stockStatus.tool, tool).groupBy(tool.subGroup.id, stockStatus.currentDay.month())
						.select(stockStatus.currentDay.max())))
				.leftJoin(stockStatus.tool, tool).leftJoin(tool.subGroup, subGroup)
				.leftJoin(subGroup.mainGroup, mainGroup)
				.groupBy(mainGroup.id, mainGroup.name, stockStatus.currentDay.month()).fetch();

		List<StockStatusSummaryByMainGroupDto> dtoList = results.stream()
				.map(tuple -> new StockStatusSummaryByMainGroupDto(tuple.get(stockStatus.toolbox.name),
						tuple.get(stockStatus.currentDay), tuple.get(mainGroup.name),
						tuple.get(stockStatus.totalCount.sum()), tuple.get(stockStatus.rentalCount.sum()),
						tuple.get(stockStatus.goodCount.sum()),
						tuple.get(stockStatus.damageCount.sum().add(stockStatus.faultCount.sum()))))
				.collect(Collectors.toList());

		return null;
	}
}
