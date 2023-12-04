package com.care4u.toolbox.tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolRepository;
import com.care4u.toolbox.stock_status.StockStatus;
import com.care4u.toolbox.stock_status.StockStatusRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

	private final Logger logger = LoggerFactory.getLogger(TagService.class);
	
	private final TagRepository repository;
	private final StockStatusRepository stockStatusRepository;
	private final ToolRepository toolRepository;
	private final ToolboxRepository toolboxRepository;
	private final RentalToolRepository rentalToolRepository;
	
	@Transactional(readOnly = true)
	public TagDto get(long id){
		Optional<Tag> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new TagDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public TagDto get(String macaddress){		
		Tag item = repository.findByMacaddress(macaddress);
		if (item == null) {
			logger.error("Invalid macaddress : " + macaddress);
			return null;
		}
		
		return new TagDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<TagDto> listByToolboxId(long toolboxId) {
		List<Tag> list = repository.findAllByToolboxId(toolboxId);
		return getDtoList(list);
	}
	
	private List<TagDto> getDtoList(List<Tag> list){
		List<TagDto> dtoList = new ArrayList<TagDto>();
		for (Tag item : list) {
			dtoList.add(convertToDto(item));
		}
		return dtoList;
	}
	
	@Transactional
	private TagDto convertToDto(Tag tag) {
		if (tag.getRentalTool()!=null) {
			List<Tag> tagList = repository.findAllByRentalToolId(tag.getRentalTool().getId());
			String tags = tagList.stream()
			.map(t -> t.getMacaddress())
			.collect(Collectors.joining(","));
			return new TagDto(tag, tags);
		}else {
			return new TagDto(tag);
		}
	}
	
	@Transactional
	public TagDto updateRentalTool(TagDto tagDto) {
		Optional<Tag> tagOptional = repository.findById(tagDto.getId());
		if (tagOptional.isEmpty()) {
			logger.error("Invalid Id: "+tagDto.getId());
			return null;
		}
		Tag tag = tagOptional.get();
		if (tagDto.getRentalToolDto()==null) {
			tag.updateRentalTool(null);
		}else {	
			Optional<RentalTool> rentalTool = rentalToolRepository.findById(tagDto.getRentalToolDto().getId());
			if (rentalTool.isEmpty()) {
				logger.error("Invalid Id: "+tagDto.getRentalToolDto().getId());
				return null;
			}
			tag.updateRentalTool(rentalTool.get());
		}
		return convertToDto(repository.save(tag));
	}
	@Transactional
	public TagDto updateToolbox(TagDto tagDto) {
		Optional<Tag> tagOptional = repository.findById(tagDto.getId());
		if (tagOptional.isEmpty()) {
			logger.error("Invalid Id: "+tagDto.getId());
			return null;
		}
		Tag tag = tagOptional.get();
		if (tagDto.getToolboxDto()==null) {
			logger.error("Toolbox Null");
			return null;
		}else {	
			Optional<Toolbox> toolbox = toolboxRepository.findById(tagDto.getToolboxDto().getId());
			if (toolbox.isEmpty()) {
				logger.error("Invalid Id: "+tagDto.getToolboxDto().getId());
				return null;
			}
			tag.updateToolbox(toolbox.get());
		}
		return convertToDto(repository.save(tag));
	}
	@Transactional
	public Tag addNew(String macAddress) {
        if (repository.findByMacaddress(macAddress)!=null) {
        	logger.error("Already Exists : "+macAddress);
        	return null;
        }
        
		String[] parts = macAddress.split("_");
        long toolboxId = Long.parseLong(parts[0]); 
        long toolId = Long.parseLong(parts[1]);
        long instanceId = Long.parseLong(parts[2]);
        
        Optional<Tool> tool = toolRepository.findById(toolId);
        Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
        if (tool.isEmpty()) {
        	logger.error("tool doesn't exist");
        	return null;
        }
        if (toolbox.isEmpty()) {
        	logger.error("toolbox doesn't exist");
        	return null;
        }
        StockStatus stock = stockStatusRepository.findByToolIdAndToolboxIdAndCurrentDay(toolId, toolboxId, LocalDate.now());
		if (stock == null) {
			logger.error("stock not found");
			return null;
		}
		long tagCount = repository.countByToolIdAndToolboxId(toolId,toolboxId);
		if (tagCount>=stock.getTotalCount()) {
			logger.error("All Tool already has Tags : "+tagCount+"/"+stock.getTotalCount());
			return null;
		}
		Tag tag = Tag.builder()
		.macaddress(macAddress)
		.tool(tool.get())
		.toolbox(toolbox.get())
		.build();
		return repository.save(tag);
	}
	
	@Transactional
	public Tag addNew(Tool tool, Toolbox toolbox) {
		StockStatus stock = stockStatusRepository.findByToolIdAndToolboxIdAndCurrentDay(tool.getId(), toolbox.getId(), LocalDate.now());
		if (stock == null) {
			logger.error("stock not found");
			return null;
		}
		long tagCount = repository.countByToolIdAndToolboxId(tool.getId(),toolbox.getId());
		if (tagCount>=stock.getTotalCount()) {
			logger.error("All Tool already has Tags : "+tagCount+"/"+stock.getTotalCount());
			return null;
		}
		Tag tag = Tag.builder()
		.tool(tool)
		.toolbox(toolbox)
		.macaddress(toolbox.getId()+"_"+tool.getId()+"_"+tagCount)
		.build();
		return repository.save(tag);
	}
	/**
	 * 초기 모의 데이터 생성용입니다. > Care4UManager에서 1회 사용 후 폐기
	 * @deprecated
	 */
	@Transactional
	public void addMock() {
		List<StockStatus> stocks = stockStatusRepository.findAllByCurrentDay(LocalDate.now());
		
		Random random = new Random();
		int debugCount = 0;
		for (StockStatus stock : stocks) {
			if (random.nextInt(stocks.size())<stocks.size()*0.4) {
				for (int i = random.nextInt(stock.getTotalCount());i>=0;i--) {
					Tag tag = addNew(stock.getTool(),stock.getToolbox());
					logger.info("item "+debugCount + " added. / " +stock.getToolbox().getId()+"_"+stock.getTool().getId()+"_"+i +" / " +tag.getMacaddress());
					debugCount++;
				}
			}
		}
		logger.info("Complete, total " + debugCount +" items added");
	}
}
