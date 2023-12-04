package com.care4u.toolbox.sheet.return_tool;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.constant.ToolState;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tag.Tag;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagRepository;
import com.care4u.toolbox.tag.TagService;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnToolService {

	private final Logger logger = LoggerFactory.getLogger(ReturnToolService.class);
	
	private final ReturnToolRepository repository;
	private final ToolRepository toolRepository;
	private final RentalToolRepository rentalToolRepository;
	private final TagRepository tagRepository;
	private final TagService tagService;
	private final StockStatusService stockStatusService;
	private final RentalToolService rentalToolService;
	
	@Transactional(readOnly = true)
	public ReturnToolDto get(long id){
		Optional<ReturnTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		List<Tag> rentalTagList = tagRepository.findAllByRentalToolId(item.get().getRentalTool().getId());
		String rentalTags = rentalTagList.stream()
		.map(tag -> tag.getMacaddress())
		.collect(Collectors.joining(","));
		
		return new ReturnToolDto(item.get(),rentalTags);
	}
	
	@Transactional
	public ReturnTool addNew(ReturnToolFormDto toolDto, ReturnSheet savedReturnSheet) {
		Optional<RentalTool> rentalTool = rentalToolRepository.findById(toolDto.getRentalToolId());
		if (rentalTool.isEmpty()){
			logger.error("rentalTool not found");
			return null;
		}
		
		ToolState status = toolDto.getStatus();
		int count = toolDto.getCount();
		
		ReturnTool returnTool = ReturnTool.builder()
				.returnSheet(savedReturnSheet)
				.rentalTool(rentalTool.get())
				.goodCount(status.equals(ToolState.GOOD)?count:0)
				.faultCount(status.equals(ToolState.FAULT)?count:0)
				.damageCount(status.equals(ToolState.DAMAGE)?count:0)
				.lossCount(status.equals(ToolState.LOSS)?count:0)
				.discardCount(status.equals(ToolState.DISCARD)?count:0)
				.count(count)
				.Tags(toolDto.getTags())
				.build();
		
		ReturnTool savedReturnTool=repository.save(returnTool);
		
		if (toolDto.getTags() != null && toolDto.getTags().length() > 0) {
			String[] tags = toolDto.getTags().split(",");
			for (String tagString : tags) {
				Tag tag = tagRepository.findByMacaddress(tagString);
				if (tag == null) {
					logger.error("Tag not found");
					return null;
				}
				
				if (!tag.getToolbox().equals(savedReturnSheet.getToolbox())){
					tag.updateToolbox(savedReturnSheet.getToolbox());
					tagService.updateToolbox(new TagDto(tag));
				}
				tag.updateRentalTool(null);
				tagService.updateRentalTool(new TagDto(tag));
				
				logger.info("tag ["+tag.getMacaddress() + "] returned.");
			}
		}
		
		StockStatusDto stockDto = stockStatusService.get(savedReturnTool.getRentalTool().getTool().getId(),savedReturnSheet.getToolbox().getId());
		stockStatusService.returnItems(stockDto.getId(), savedReturnTool.getGoodCount(), savedReturnTool.getFaultCount(), savedReturnTool.getDamageCount(), savedReturnTool.getDiscardCount(), savedReturnTool.getLossCount());
		
		rentalToolService.returnUpdate(rentalTool.get(),count);
		
		return savedReturnTool;
	}
}
