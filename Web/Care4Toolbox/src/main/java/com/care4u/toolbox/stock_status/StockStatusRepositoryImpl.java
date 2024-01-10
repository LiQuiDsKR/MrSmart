package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.core.types.Projections;
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
			
			/*
		rentalCount = queryFactory
			    .select(rentalTool.count.sum())
			    .from(rentalSheet)
			    .join(rentalTool).on(rentalSheet.id.eq(rentalTool.rentalSheet.id))
			    .where(rentalSheet.worker.eq(membership)
			    	.and(rentalTool.tool.eq(tool))
			    	.and(rentalSheet.toolbox.id.eq(toolboxId)))
			    .fetchOne();

		returnCount = queryFactory
			    .select(returnTool.count.sum())
			    .from(returnSheet)
			    .join(returnTool).on(returnSheet.id.eq(returnTool.returnSheet.id))
			    .where(returnSheet.worker.eq(membership)
				    	.and(returnTool.rentalTool.tool.eq(tool))
				    	.and(returnSheet.toolbox.id.eq(toolboxId)))
			    .fetchOne();
			    */
		
		List<Tuple> rentalResults = queryFactory.select(rentalTool.rentalSheet.eventTimestamp, rentalTool.count.sum())
                .from(rentalTool)
                .join(rentalTool.rentalSheet, rentalSheet)
                .where(rentalSheet.worker.eq(membership)
    			    	.and(rentalTool.tool.eq(tool))
    			    	.and(rentalSheet.toolbox.id.eq(toolboxId)))
                .groupBy(rentalTool.rentalSheet.eventTimestamp)
                .fetch();

		List<Tuple> returnResults = queryFactory.select(returnSheet.eventTimestamp, returnTool.count.sum())
                .from(returnTool)
                .join(returnTool.returnSheet, returnSheet)
                .where(returnSheet.worker.eq(membership)
    			    	.and(returnTool.rentalTool.tool.eq(tool))
    			    	.and(returnSheet.toolbox.id.eq(toolboxId)))
                .groupBy(returnSheet.eventTimestamp)
                .fetch();
		
		//3. 추가적인 데이터 처리
		//stock_status 테이블에서 데이터를 가져와서, 존재하지 않는 날짜에 대해 0을 할당합니다.
		List<Tuple> stockResults = queryFactory.select(stockStatus.currentDay, Expressions.constant(0), Expressions.constant(0))
				.distinct()
                .from(stockStatus)
                .fetch();
		
		//4. QueryDSL 문법 적용
		//최종적으로 모든 결과를 하나의 리스트로 합치고, 날짜별로 정렬합니다.
		List<Tuple> resultQuery = new ArrayList<>();
		resultQuery.addAll(rentalResults);
		resultQuery.addAll(returnResults);
		resultQuery.addAll(stockResults);
		
		resultQuery.sort(Comparator.comparing(o -> o.get(0, LocalDate.class)));
		
		List<StockStatusSummaryByToolStateDto> finalList = convertToDtoList(resultQuery);

		return finalList;
		} catch (Exception e){
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
		
/*
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
		*/
		
		return tempList;
	}
	
	private List<StockStatusSummaryByToolStateDto> convertToDtoList(List<Tuple> tuples) {
	    List<StockStatusSummaryByToolStateDto> dtoList = new ArrayList<>();
	    for (Tuple tuple : tuples) {
	        LocalDate date = tuple.get(0, LocalDate.class);
	        int rentalCount = tuple.get(1, Integer.class);
	        int returnCount = tuple.get(2, Integer.class);

	        StockStatusSummaryByToolStateDto dto = new StockStatusSummaryByToolStateDto();
	        dto.setCurrentDay(date);
	        dto.setRentalCount(rentalCount);
	        dto.setReturnCount(returnCount);

	        dtoList.add(dto);
	    }
	    return dtoList;
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
		List<BooleanExpression> conditions = new ArrayList<>();

		for (String keyword : keywords) {
			BooleanExpression condition = stock.tool.name.contains(keyword);
			conditions.add(condition);
		}

		BooleanExpression finalCondition = Expressions.allOf(conditions.toArray(new BooleanExpression[0]));

		List<StockStatus> content = queryFactory.selectFrom(stock)
				.where(
						finalCondition
						.and(stock.toolbox.id.eq(toolboxId))
						.and(stock.currentDay.eq(date))
				)
				.orderBy(QTool.tool.id.asc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
				.fetch();
		long total = queryFactory.select(Wildcard.count).from(stock)
				.where(finalCondition
				.and(stock.toolbox.id.eq(toolboxId))
				.and(stock.currentDay.eq(date))).fetchOne();
		return new PageImpl<>(content, pageable, total);
	}
}
