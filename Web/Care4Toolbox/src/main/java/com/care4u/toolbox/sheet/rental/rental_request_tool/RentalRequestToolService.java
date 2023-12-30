package com.care4u.toolbox.sheet.rental.rental_request_tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalRequestToolService {

	private final Logger logger = LoggerFactory.getLogger(RentalRequestToolService.class);
	
	private final RentalRequestToolRepository repository;
	
	@Autowired
	private final ToolRepository toolRepository;
	
	@Autowired
	private final StockStatusService stockStatusService;
	
	@Transactional(readOnly = true)
	public RentalRequestToolDto get(long id){
		Optional<RentalRequestTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalRequestToolDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public List<RentalRequestTool> list(long toolId){
		return repository.findAllByToolId(toolId);
	}
	
	@Transactional
	public RentalRequestTool addNew(RentalRequestToolFormDto formDto, RentalRequestSheet sheet) {
		Optional<Tool> tool = toolRepository.findById(formDto.getToolDtoId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			return null;
		}
		
		RentalRequestTool rentalRequestTool = RentalRequestTool.builder()
				.rentalRequestSheet(sheet)
				.tool(tool.get())
				.count(formDto.getCount())
				.Tags("")
				.build();
		
		StockStatusDto stockDto = stockStatusService.get(rentalRequestTool.getTool().getId(),sheet.getToolbox().getId());
		stockStatusService.requestItems(stockDto.getId(), rentalRequestTool.getCount());
		
		return repository.save(rentalRequestTool);
	}

}
