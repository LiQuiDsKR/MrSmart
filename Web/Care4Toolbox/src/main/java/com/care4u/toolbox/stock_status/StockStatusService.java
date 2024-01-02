package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolRepository;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabel;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelRepository;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StockStatusService {

	private final Logger logger = LoggerFactory.getLogger(StockStatusService.class);
	
	private final StockStatusRepository repository;
	private final ToolboxRepository toolboxRepository;
	private final ToolRepository toolRepository;
	private final SubGroupRepository subGroupRepository;
	
	@Transactional(readOnly = true)
	public StockStatusDto get(long id){
		Optional<StockStatus> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid StockStatus id : " + id);
			return null;
		}
		
		return new StockStatusDto(item.get());
	}

	@Transactional(readOnly = true)
	public StockStatusDto get(long toolId, long toolboxId) {
		LocalDate date = LocalDate.now();
		
		StockStatus status= repository.findByToolIdAndToolboxIdAndCurrentDay(toolId, toolboxId, date);
		if (status==null) {
			logger.error("Invalid StockStatus id : "+toolId+","+toolboxId);
			return null;
		}
	
		return new StockStatusDto(status);
	}
	@Transactional(readOnly=true)
	public Page<StockStatusDto> getTodayPage(long toolboxId, String searchString, List<Long> subGroupIds, Pageable pageable){
		for (long i : subGroupIds) {
			if (subGroupRepository.findById(i).isEmpty()) {
				logger.error("invalid subGroupId : "+i);
				return null;
			}
		}
		Page<StockStatus> stockPage = repository.findAllByToolboxIdAndCurrentDay(toolboxId, LocalDate.now(), searchString, subGroupIds, pageable);
		return stockPage.map(e->new StockStatusDto(e));
	}
	
	@Transactional
	public StockStatusDto requestItems(long id, int count) {
		StockStatus stock;
		Optional<StockStatus> stockOptional=repository.findById(id);
		if (stockOptional.isEmpty()) {
			logger.error("Invalid StockStatus id : "+id);
			return null;
		}
		stock=stockOptional.get();
		if (count>stock.getGoodCount()) {
			logger.error("재고 없음" + stock.getGoodCount()+")");
			return null;
		}
		stock.requestUpdate(count);
		return new StockStatusDto(repository.save(stock));
	}
	@Transactional
	public StockStatusDto requestCancelItems(long id, int count) {
		StockStatus stock;
		Optional<StockStatus> stockOptional=repository.findById(id);
		if (stockOptional.isEmpty()) {
			logger.error("Invalid StockStatus id : "+id);
			return null;
		}
		stock=stockOptional.get();
		stock.requestCancelUpdate(count);
		return new StockStatusDto(repository.save(stock));
	}
	@Transactional
	public StockStatusDto rentItems(long id, int count) {
		StockStatus stock;
		Optional<StockStatus> stockOptional=repository.findById(id);
		if (stockOptional.isEmpty()) {
			logger.error("Invalid StockStatus id : "+id);
			return null;
		}
		stock=stockOptional.get();
		if (count>stock.getGoodCount()) {
			logger.error("request count("+count+") is over stock(" + stock.getGoodCount()+")");
			return null;
		}
		stock.rentUpdate(count);
		return new StockStatusDto(repository.save(stock));
	}
	@Transactional
	public StockStatusDto returnItems(long id, int goodCount, int faultCount, int damageCount, int discardCount, int lossCount) {
		StockStatus stock;
		Optional<StockStatus> stockOptional=repository.findById(id);
		if (stockOptional.isEmpty()) {
			logger.error("Invalid StockStatus id : "+id);
			return null;
		}
		stock=stockOptional.get();
		stock.returnUpdate(goodCount, faultCount, damageCount, discardCount, lossCount);
		return new StockStatusDto(repository.save(stock));
	}
	@Transactional
	public StockStatusDto buyItems(long toolId, long toolboxId, int count) {
		StockStatus stock=repository.findByToolIdAndToolboxIdAndCurrentDay(toolId,toolboxId,LocalDate.now());
		if (stock==null) {
			stock=addNew(toolId, toolboxId, 0);
		}
		stock.buyUpdate(count);
		return new StockStatusDto(repository.save(stock));
	}
	@Transactional
	public StockStatusDto supplyItems(long id, int count) {
		StockStatus stock;
		Optional<StockStatus> stockOptional=repository.findById(id);
		if (stockOptional.isEmpty()) {
			logger.error("Invalid StockStatus id : "+id);
			return null;
		}
		stock=stockOptional.get();
		if (count>stock.getGoodCount()) {
			logger.error("request count("+count+") is over stock(" + stock.getGoodCount()+")");
			return null;
		}
		stock.supplyUpdate(count);
		return new StockStatusDto(repository.save(stock));
	}
	
@Scheduled(cron = "00 00 12 * * ?") // 매일 자정에 실행

    public void copyEntities() {
		
		LocalDate latestDate = repository.getLatestCurrentDay();
		LocalDate currentDate = latestDate;

		while (!currentDate.isAfter(LocalDate.now().minusDays(1))) {
		    currentDate = currentDate.plusDays(1);
			LocalDate formerDate = currentDate.minusDays(1);
			LocalDate latterDate = currentDate;
	        List<StockStatus> formerStatus = repository.findAllByCurrentDay(formerDate);
	        int count = 0;
	        for (StockStatus former : formerStatus) {
	        	StockStatus latter;
	        	if (isCorrect(former)) {
	        		latter=StockStatus.builder()
	        				.toolbox(former.getToolbox())
	        				.tool(former.getTool())
	        				.buyCount(0)
	        				.damageCount(former.getDamageCount())
	        				.discardCount(0)
	        				.faultCount(former.getFaultCount())
	        				.goodCount(former.getGoodCount())
	        				.lossCount(0)
	        				.rentalCount(former.getRentalCount())
	        				.totalCount(former.getTotalCount())
	        				.currentDay(latterDate)
	        				.build();
	        		repository.save(latter);
	        		logger.info(count+" : "+former.toString()+" -> "+latter.toString());
	        		count++;
	        	}
	        }
	        logger.info(count+"/"+formerStatus.size());
		}
    }
	
	private boolean isCorrect(StockStatus stockStatus) {
		LocalDate currentDate = stockStatus.getCurrentDay();
		
		return true;
	}
	
	/*
	@Transactional(readOnly=true)
	public List<StockStatusTimeChartDto> getTimeChart(LocalDate startDate, LocalDate endDate){
		for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
			List<StockStatus> currentList = repository.findAllByCurrentDay(currentDate);
			for (StockStatus stock : currentList) {
				
			}
		}
		
	}
	*/
	
	/**
	 * 반드시 매입 Or 초기화 때만 호출해야 함
	 */
	@Transactional
	public StockStatus addNew(long toolId, long toolboxId, int count) {
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxId);
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolId);
			return null;
		}
		
		StockStatus findStock=repository.findByToolIdAndToolboxIdAndCurrentDay(toolId, toolboxId, LocalDate.now());
		if (findStock!=null) {
			logger.error("already exists! : "+toolId+","+toolboxId);
			return null;
		}
		
		StockStatus stock = StockStatus.builder()
		.toolbox(toolbox.get())
		.tool(tool.get())
		.buyCount(0)
		.damageCount(0)
		.discardCount(0)
		.faultCount(0)
		.goodCount(count)
		.lossCount(0)
		.rentalCount(0)
		.totalCount(count)
		.currentDay(LocalDate.now())
		.build();
		
		repository.save(stock);
		
		return stock;
	}
	
	@Transactional(readOnly=true)
	public StockStatusSummaryByToolStateDto getSummary(long toolboxId, LocalDate currentDate) {
		return repository.getStockStatusSummaryByToolStateDto(toolboxId, currentDate);
	}
	@Transactional(readOnly=true)
	public List<StockStatusSummaryByToolStateDto> getSummary(long toolboxId, LocalDate startDate, LocalDate endDate) {
		return repository.getStockStatusSummary(toolboxId, startDate, endDate);
	}
	@Transactional(readOnly=true)
	public List<StockStatusSummaryByMainGroupDto> getStockStatusSummaryByMainGroupDto(Long toolboxId, LocalDate currentLocalDate) {
		return repository.getStockStatusSummaryByMainGroupDto(toolboxId, currentLocalDate);
	}
	
	/**
	 * 초기 모의 데이터 생성용입니다. > Care4UManager에서 1회 사용 후 폐기
	 * @deprecated
	 */
	@Transactional
	public void addMock() {
		List<Tool> toolList = toolRepository.findAll();
		List<Toolbox> toolboxList = toolboxRepository.findAll();
		Random random = new Random();
		int toolboxSize= toolboxList.size();
		int minValue = 100;
        int maxValue = 100;
        int debugCount = 0;
		for (Tool tool : toolList) {
	        int randomI = random.nextInt(toolboxSize);
	        List<Toolbox> copiedList = new ArrayList<Toolbox>(toolboxList);
	        for (int i = 0 ; i < randomI ; i++) {
	        	copiedList.remove(random.nextInt(copiedList.size()));
	        }
	        for (Toolbox toolbox : copiedList) {
		        int randomCount = random.nextInt(maxValue - minValue + 1) + minValue;
				new StockStatus();
				StockStatus newStatus = StockStatus.builder()
						.currentDay(LocalDate.now())
						.totalCount(randomCount)
						.goodCount(randomCount)
						.buyCount(0)
						.damageCount(0)
						.discardCount(0)
						.faultCount(0)
						.lossCount(0)
						.rentalCount(0)
						.tool(tool)
						.toolbox(toolbox)
						.build()
						;
				repository.save(newStatus);
				logger.info(debugCount + " : " + newStatus.toString());
				debugCount++;
	        }
		}
		logger.info("Complete, total " + debugCount +" items added");
	}

}
