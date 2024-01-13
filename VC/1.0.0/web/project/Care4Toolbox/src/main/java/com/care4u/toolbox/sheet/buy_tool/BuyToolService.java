package com.care4u.toolbox.sheet.buy_tool;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.buy_sheet.BuySheet;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.stock_status.StockStatus;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusRepository;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BuyToolService {

	private final Logger logger = LoggerFactory.getLogger(BuyToolService.class);
	
	private final BuyToolRepository repository;
	private final ToolRepository toolRepository;
	private final StockStatusRepository stockStatusRepository;
	private final StockStatusService stockStatusService;
	
	@Transactional(readOnly = true)
	public BuyToolDto get(long id){
		Optional<BuyTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new BuyToolDto(item.get());
	}

	@Transactional
	public BuyTool addNew(BuyToolFormDto formDto, BuySheet sheet) {
		Optional<Tool> tool = toolRepository.findById(formDto.getToolDtoId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			return null;
		}
		
		BuyTool buyTool = BuyTool.builder()
				.buySheet(sheet)
				.tool(tool.get())
				.count(formDto.getCount())
				.build();
		
		BuyTool savedBuyTool= repository.save(buyTool);
		
		stockStatusService.buyItems(formDto.getToolDtoId(), sheet.getToolbox().getId(), savedBuyTool.getCount());
		
		return repository.save(buyTool);
	}
}