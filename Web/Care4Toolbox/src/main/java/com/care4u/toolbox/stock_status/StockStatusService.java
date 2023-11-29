package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
	private final ToolboxToolLabelRepository toolboxToolLabelRepository;
	
	private final ToolboxToolLabelService toolboxToolLabelService;
	
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
		}
	
		return new StockStatusDto(status);
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
		return null;
	}
	@Transactional
	public StockStatusDto buyItems(long id, int count) {
		return null;
	}
	
	@Scheduled(cron = "1 42 10 * * ?") // 매일 자정에 실행
    public void copyEntities() {
		LocalDate formerDate = LocalDate.now().minusDays(1);
		LocalDate latterDate = LocalDate.now();
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
	
	private boolean isCorrect(StockStatus stockStatus) {
		//그날 있었던 Rental과 Return 전부 조회 후 개수 비교 및 합산.
		//자정 업데이트간 사용하는 검증절차.
		return true;
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
		int minValue = 1;
        int maxValue = 20;
        
		for (Tool tool : toolList) {
	        int randomCount = random.nextInt(maxValue - minValue + 1) + minValue;
	        int randomI = random.nextInt(toolboxSize);
	        List<Toolbox> copiedList = new ArrayList<Toolbox>(toolboxList);
	        for (int i = 0 ; i < randomI ; i++) {
	        	copiedList.remove(random.nextInt(copiedList.size()));
	        }
	        for (Toolbox toolbox : copiedList) {
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
	        }
		}
	}
}
