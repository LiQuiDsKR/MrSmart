package com.care4u.toolbox.sheet.supply_tool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheet;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplyToolService {

	private final Logger logger = LoggerFactory.getLogger(SupplyToolService.class);
	
	private final SupplyToolRepository repository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public SupplyToolDto get(long id){
		Optional<SupplyTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new SupplyToolDto(item.get());
	}

	@Transactional
	public SupplyTool addNew(RentalRequestToolDto requestDto, SupplySheet sheet) {
		Optional<Tool> tool = toolRepository.findById(requestDto.getToolDto().getId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			return null;
		}
		
		SupplyTool supplyTool = SupplyTool.builder()
				.supplySheet(sheet)
				.tool(tool.get())
				.count(requestDto.getCount())
				.replacementDate(null)
				.build();
		
		return repository.save(supplyTool);
	}
}
