package com.care4u.toolbox.sheet.rental.rental_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalToolService {

	private final Logger logger = LoggerFactory.getLogger(RentalToolService.class);
	
	private final RentalToolRepository repository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public RentalToolDto get(long id){
		Optional<RentalTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalToolDto(item.get());
	}
	
	@Transactional
	public RentalTool addNew(RentalRequestToolDto requestDto, RentalSheet sheet) {
		Optional<Tool> tool = toolRepository.findById(requestDto.getToolDto().getId());
		if (tool.isEmpty()){
			logger.error("tool not found");
			return null;
		}
		
		RentalTool rentalTool = RentalTool.builder()
				.rentalSheet(sheet)
				.tool(tool.get())
				.count(requestDto.getCount())
				.outstandingCount(requestDto.getCount())
				.Tags(requestDto.getTags())
				.build();
		
		return repository.save(rentalTool);
	}


}
