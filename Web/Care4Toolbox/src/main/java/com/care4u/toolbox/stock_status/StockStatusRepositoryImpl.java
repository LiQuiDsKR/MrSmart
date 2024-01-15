package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.group.main_group.QMainGroup;
import com.care4u.toolbox.group.sub_group.QSubGroup;
import com.care4u.toolbox.sheet.rental.rental_sheet.QRentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.QRentalTool;
import com.care4u.toolbox.sheet.return_sheet.QReturnSheet;
import com.care4u.toolbox.sheet.return_tool.QReturnTool;
import com.care4u.toolbox.tool.QTool;
import com.care4u.toolbox.tool.Tool;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import groovyjarjarantlr4.v4.runtime.atn.SemanticContext.AND;

public class StockStatusRepositoryImpl implements StockStatusRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private final Logger logger = LoggerFactory.getLogger(StockStatusRepositoryImpl.class);
	
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
	public List<StockStatusSummaryByToolStateDto> getStockStatusSummary(Long partId, Membership membership, Tool tool,
			Long toolboxId, Boolean isWorker, Boolean isLeader, Boolean isApprover, LocalDate startDate,
			LocalDate endDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;
		QRentalSheet rentalSheet = QRentalSheet.rentalSheet;
		QRentalTool rentalTool = QRentalTool.rentalTool;
		QReturnSheet returnSheet = QReturnSheet.returnSheet;
		QReturnTool returnTool = QReturnTool.returnTool;
		
		Integer rentalCount;
		Integer returnCount;
		try {
		rentalCount = queryFactory
			    .select(rentalTool.count.sum())
			    .from(rentalSheet)
			    .join(rentalTool).on(rentalSheet.id.eq(rentalTool.rentalSheet.id))
			    .where((membership==null?Expressions.asBoolean(true).isTrue():rentalSheet.worker.eq(membership))
			    	.and(tool==null?Expressions.asBoolean(true).isTrue():rentalTool.tool.eq(tool))
			    	.and(toolboxId==0?Expressions.asBoolean(true).isTrue():rentalSheet.toolbox.id.eq(toolboxId)))
			    .fetchOne();

		returnCount = queryFactory
			    .select(returnTool.count.sum())
			    .from(returnSheet)
			    .join(returnTool).on(returnSheet.id.eq(returnTool.returnSheet.id))
			    .where((membership==null?Expressions.asBoolean(true).isTrue():returnSheet.worker.eq(membership))
				    	.and(tool==null?Expressions.asBoolean(true).isTrue():returnTool.rentalTool.tool.eq(tool))
				    	.and(toolboxId==0?Expressions.asBoolean(true).isTrue():returnSheet.toolbox.id.eq(toolboxId)))
			    .fetchOne();
		} catch (Exception e){
			logger.debug(e.getMessage());
			e.printStackTrace();
			rentalCount=0;
			returnCount=0;
		}

		
		List<StockStatusSummaryByToolStateDto> tempList= queryFactory
				.select(Projections.constructor(StockStatusSummaryByToolStateDto.class, stockStatus.toolbox.name,
						stockStatus.currentDay, stockStatus.totalCount.sum(), stockStatus.rentalCount.sum().add(stockStatus.supplyCount.sum()),
						stockStatus.buyCount.sum(), stockStatus.goodCount.sum(), stockStatus.faultCount.sum(),
						stockStatus.damageCount.sum(), stockStatus.lossCount.sum(), stockStatus.discardCount.sum(),
						stockStatus.supplyCount.sum(), stockStatus.returnCount.sum()))
				.from(stockStatus)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.between(startDate, endDate)))
				.groupBy(stockStatus.currentDay).fetch();
		

		List<StockStatusSummaryByToolStateDto> resultList;
		if(tool!=null || membership!=null || partId!=0) {
		resultList = new ArrayList<>();
			for (StockStatusSummaryByToolStateDto item : tempList) {
				StockStatusSummaryByToolStateDto newItem = new StockStatusSummaryByToolStateDto(item); // 복제 생성자를 통해 객체 복제
			    newItem.setRentalCount(rentalCount);
			    newItem.setReturnCount(returnCount);
			    resultList.add(newItem); // 결과 리스트에 추가
			}
		}else {
			resultList=tempList;
		}
		
		
		return resultList;
	}

	@Override
	public StockStatusSummaryByToolStateDto getStockStatusSummaryByToolStateDto(long toolboxId, LocalDate currentDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;

		return queryFactory
				.select(Projections.constructor(StockStatusSummaryByToolStateDto.class, stockStatus.toolbox.name,
						stockStatus.currentDay, stockStatus.totalCount.sum(), stockStatus.rentalCount.sum(),
						stockStatus.buyCount.sum(), stockStatus.goodCount.sum(), stockStatus.faultCount.sum(),
						stockStatus.damageCount.sum(), stockStatus.lossCount.sum(), stockStatus.discardCount.sum(),
						stockStatus.supplyCount.sum(),stockStatus.returnCount.sum()))
				.from(stockStatus)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.eq(currentDate))).fetchOne();
	}

	
	//대분류별 일간 통계. -> 금일 현황 페이지를 담당.
	@Override
	public List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDto(long toolboxId,
			LocalDate currentDate) {
		QStockStatus stockStatus = QStockStatus.stockStatus;
		QTool tool = QTool.tool;
		QSubGroup subGroup = QSubGroup.subGroup;
		QMainGroup mainGroup = QMainGroup.mainGroup;

		List<Tuple> results = queryFactory
				.select(stockStatus.toolbox.name,
						stockStatus.currentDay,
						mainGroup.name,
						stockStatus.totalCount.sum(),
						stockStatus.goodCount.sum(),
						stockStatus.rentalCount.sum(),
						stockStatus.damageCount.sum().add(stockStatus.faultCount.sum()),
						stockStatus.returnCount.sum()
						)
				.from(stockStatus).leftJoin(stockStatus.tool, tool).leftJoin(tool.subGroup, subGroup)
				.leftJoin(subGroup.mainGroup, mainGroup)
				.where(stockStatus.toolbox.id.eq(toolboxId).and(stockStatus.currentDay.eq(currentDate)))
				.groupBy(mainGroup.id, mainGroup.name).fetch();

		List<StockStatusSummaryByMainGroupDto> dtoList = results.stream()
				.map(tuple -> new StockStatusSummaryByMainGroupDto(
						tuple.get(stockStatus.toolbox.name),
						tuple.get(stockStatus.currentDay),
						tuple.get(mainGroup.name),
						tuple.get(stockStatus.totalCount.sum()),
						tuple.get(stockStatus.goodCount.sum()),
						tuple.get(stockStatus.rentalCount.sum()),
						tuple.get(stockStatus.damageCount.sum().add(stockStatus.faultCount.sum())),
						tuple.get(stockStatus.returnCount.sum())
						))
				.collect(Collectors.toList());

		return dtoList;
	}

	//대분류별 & 월별 통계 (미사용)
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
						tuple.get(stockStatus.totalCount.sum()), 
						tuple.get(stockStatus.goodCount.sum()),tuple.get(stockStatus.rentalCount.sum()),
						tuple.get(stockStatus.damageCount.sum().add(stockStatus.faultCount.sum())), 
						tuple.get(stockStatus.returnCount.sum())))
				.collect(Collectors.toList());

		return null;
	}

	//이름으로 검색 쿼리. -> 타 공구실 재고 조회 페이지.
	@Override
	public Page<StockStatus> findAllByToolboxIdAndCurrentDay(Long toolboxId, LocalDate date, String toolName,
			List<Long> subGroupIds, Pageable pageable) {
		QTool tool = QTool.tool;
		QStockStatus stock = QStockStatus.stockStatus;
		
		String[] keywords = toolName.split(" "); // 검색어를 공백 기준으로 분리
		List<BooleanExpression> nameConditions = new ArrayList<>();
		List<BooleanExpression> engNameConditions = new ArrayList<>();
		List<BooleanExpression> specConditions = new ArrayList<>();

		for (String keyword : keywords) {
			BooleanExpression nameCondition = stock.tool.name.contains(keyword);
			nameConditions.add(nameCondition);
		}
		for (String keyword : keywords) {
			BooleanExpression engNameCondition = stock.tool.engName.contains(keyword);
			engNameConditions.add(engNameCondition);
		}
		for (String keyword : keywords) {
			BooleanExpression specCondition = stock.tool.spec.contains(keyword);
			specConditions.add(specCondition);
		}

		BooleanExpression finalNameCondition = Expressions.allOf(nameConditions.toArray(new BooleanExpression[0]));
		BooleanExpression finalEngNameCondition = Expressions.allOf(engNameConditions.toArray(new BooleanExpression[0]));
		BooleanExpression finalSpecCondition = Expressions.allOf(specConditions.toArray(new BooleanExpression[0]));

		List<StockStatus> content = queryFactory.selectFrom(stock)
				.where(
						(finalNameCondition
						.or(finalEngNameCondition)
						.or(finalSpecCondition))
						.and(stock.toolbox.id.eq(toolboxId))
						.and(stock.currentDay.eq(date))
				)
				.orderBy(QTool.tool.id.asc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
				.fetch();
		long total = queryFactory.select(Wildcard.count).from(stock)
				.where((finalNameCondition
						.or(finalEngNameCondition)
						.or(finalSpecCondition))
				.and(stock.toolbox.id.eq(toolboxId))
				.and(stock.currentDay.eq(date))).fetchOne();
		return new PageImpl<>(content, pageable, total);
	}
}
