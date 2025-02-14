package com.care4u.toolbox.sheet.rental.rental_tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetRepository;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetRepository;
import com.care4u.toolbox.stock_status.StockStatus;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagRepository;
import com.care4u.toolbox.tag.TagService;
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
	private final RentalRequestSheetRepository rentalRequestSheetRepository;
	private final TagRepository tagRepository;
	
	private final TagService tagService;
	private final StockStatusService stockStatusService;
	
	@Transactional(readOnly = true)
	public RentalToolDto get(long id){
		Optional<RentalTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		List<Tag> tagList = tagRepository.findAllByRentalToolId(id);
		
		return new RentalToolDto(item.get(),tagList);
	}
	
	@Transactional(readOnly=true)
	public List<RentalToolDto> list(long sheetId){
		List<RentalTool> toolList = repository.findAllByRentalSheetId(sheetId);
		List<RentalToolDto> dtoList = new ArrayList<RentalToolDto>();
		for (RentalTool tool : toolList) {
			List<Tag> tagList = tagRepository.findAllByRentalToolId(tool.getId());
			dtoList.add(new RentalToolDto(tool,tagList));
		}
		return dtoList;
	}
	
	/**
	 * RentalSheetService에서 호출됩니다.
	 * Tag를 생성하고, StockStatus를 변경합니다.
	 * @param requestDto
	 * @param sheet
	 * @return
	 */
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
				.rentalRequestSheet(rentalRequestSheetRepository.findById(Long.parseLong("522522")).get())
				.build();
		
		RentalTool savedRentalTool=repository.save(rentalTool);
		
		if (requestDto.getTags() != null && requestDto.getTags().length() > 0) {
			String[] tags = requestDto.getTags().split(",");
			for (String tagString : tags) {
				Tag tag = tagService.addNew(tagString);
				tag.updateRentalTool(savedRentalTool);
			}
		}
		
		StockStatusDto stockDto = stockStatusService.get(savedRentalTool.getTool().getId(),sheet.getToolbox().getId());
		stockStatusService.rentItems(stockDto.getId(), savedRentalTool.getCount());
		
		return savedRentalTool;
	}
}
