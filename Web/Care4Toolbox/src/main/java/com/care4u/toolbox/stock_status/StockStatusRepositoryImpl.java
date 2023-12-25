package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.care4u.toolbox.group.main_group.QMainGroup;
import com.care4u.toolbox.group.sub_group.QSubGroup;
import com.care4u.toolbox.tool.QTool;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class StockStatusRepositoryImpl implements StockStatusRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public StockStatusRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<StockStatusSummaryByToolStateDto> getStockStatusSummary(long toolboxId, LocalDate startDate,
			LocalDate endDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;

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
