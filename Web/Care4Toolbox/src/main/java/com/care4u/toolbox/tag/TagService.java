package com.care4u.toolbox.tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.exception.NoSuchElementFoundException;
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
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabel;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelRepository;

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
	private final ToolboxToolLabelRepository toolboxToolLabelRepository;
	private final RentalToolRepository rentalToolRepository;
	
	@Transactional(readOnly = true)
	public TagDto get(long id){
		Optional<Tag> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			throw new NoSuchElementFoundException("ID : "+id+"와 일치하는 Qrcode 데이터가 없습니다.");
		}
		
		return new TagDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public TagDto get(String macaddress){		
		Tag item = repository.findByMacaddress(macaddress);
		if (item == null) {
			logger.error("Invalid macaddress : " + macaddress);
			throw new NoSuchElementFoundException("Qr코드 : "+macaddress+"는 등록되지 않은 코드입니다.");
		}
		
		return new TagDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<TagDto> listByToolboxId(long toolboxId) {
		List<Tag> list = repository.findAllByToolboxId(toolboxId);
		return getDtoList(list);
	}
	@Transactional(readOnly = true)
	public Page<TagDto> getPage(long toolboxId, Pageable pageable){
		Page<Tag> page = repository.findAllByToolboxId(toolboxId,pageable);
		return page.map(e->convertToDto(e));
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
				logger.error("tag updated : "+tagDto.getRentalToolDto().getId()+"->"+rentalTool.get().getId());
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
	public Tag addNew(long toolId, long toolboxId, String tagString) {

//		StockStatus stock = stockStatusRepository.findByToolIdAndToolboxIdAndCurrentDay(tool.getId(), toolbox.getId(), LocalDate.now());
//		if (stock == null) {
//			logger.error("stock not found");	
//			return null;
//		}
		
		Tag tempObject = repository.findByMacaddress(tagString);
		Boolean flag = true;
		if (tempObject!=null) {
			flag =!(tempObject.getTool().getId()==toolId && tempObject.getToolbox().getId()==toolboxId);
			if(flag) {
				logger.error(tagString +" already exists!");
				throw new IllegalArgumentException("QR : "+tagString+ "는 이미 등록된 태그입니다.");
			}
		}
		
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxId);
			throw new NoSuchElementFoundException(toolboxId+"와 일치하는 공구실 데이터가 없습니다.");
		}
		
		Optional<Tool> tool = toolRepository.findById(toolId);
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolId);
			throw new NoSuchElementFoundException(toolId+"와 일치하는 공구 데이터가 없습니다.");
		}
		
		Tag tag = Tag.builder()
				.macaddress(tagString)
				.tool(tool.get())
				.toolbox(toolbox.get())
				.tagGroup("522")
				.build();
		
		logger.info("Tag added : " + tagString + " -> " + tag.getTagGroup() + " : " + tool.get().getName());

		return flag?repository.save(tag):tempObject;
	}
	
	
	@Transactional
	public void register(long toolId, long toolboxId, List<String> tagList) {
		//TODO : tagGroup살려야해
//		if (tagGroup.isEmpty()) {
//			if (tagList.size()<1) {
//				throw new IllegalArgumentException("Invalid TagGroup");
//			}
//			tagGroup=tagList.get(0);
//		}
//			tagGroup이 지금 무슨 소용임
		//TODO : 해놓고 나중에 다시 건드려야함
//		
//		List<Tag> tempList = repository.findByTagGroup(tagGroup);
//		for (Tag t : tempList) {
//			if(!tagList.contains(t.getMacaddress())) {
//				repository.delete(t);
//				logger.info("Tag Deleted : "+t.getMacaddress());
//			}
//		}
//		for (String tag : tagList) {
//			addNew(toolId, toolboxId, tag, tagGroup);
//		}
		
		for (String tag : tagList) {
			addNew(toolId, toolboxId, tag);
		}
		return;
	}
	@Transactional(readOnly=true)
	public List<TagDto> getSiblings(String tagString) {
		Tag tag = repository.findByMacaddress(tagString);
		if (tag==null) {
			logger.error("Invalid tag : "+tagString);
			return null;
		}
		//return repository.findByTagGroup(tag.getTagGroup()).stream().map(e->convertToDto(e)).collect(Collectors.toList());
		return repository.findAllByToolIdAndToolboxId(tag.getTool().getId(), tag.getToolbox().getId()).stream().map(e->convertToDto(e)).collect(Collectors.toList());
	}
	@Transactional(readOnly=true)
	public String getTagGroup(String tagString) {
		Tag tag = repository.findByMacaddress(tagString);
		if (tag==null) {
			logger.error("Invalid tag : "+tagString);
			return null;
		}
		return tag.getTagGroup();
	}
	
	@Transactional(readOnly=true)
	public long getCount(long toolboxId) {
		return repository.countByToolboxId(toolboxId);
	}

	@Transactional(readOnly=true)
	public List<TagDto> listByToolIdAndToolboxId(long toolId, long toolboxId) {
		List<Tag> list = repository.findAllByToolIdAndToolboxId(toolId, toolboxId);
		logger.info("listByToolIdAndToolboxId : "+list.size());
		return list.stream().map(e->new TagDto(e)).toList();
	}
	
	@Transactional(readOnly=true)
	public List<TagDto> listByToolboxToolLabelQrcode(String toolboxToolLabel) {
		ToolboxToolLabel label = toolboxToolLabelRepository.findByQrcode(toolboxToolLabel);
		if (label == null) {
			logger.error("Invalid toolboxToolLabel : " + toolboxToolLabel);
			throw new NoSuchElementFoundException(toolboxToolLabel+"와 일치하는 선반 QR코드 데이터가 없습니다. 기준정보를 다시 불러와주세요.");
		}
		long toolboxId = label.getToolbox().getId();
		long toolId = label.getTool().getId();
		
		List<Tag> list = repository.findAllByToolIdAndToolboxId(toolId, toolboxId);
		return list.stream().map(e->new TagDto(e)).toList();
	}

	
	@Transactional(readOnly=true)
	public List<TagDto> listByTagMacaddress(String tagMacaddress) {
		Tag tag = repository.findByMacaddress(tagMacaddress);
		if (tag == null) {
			logger.error("Invalid tagMacaddress : " + tagMacaddress);
			throw new NoSuchElementFoundException(tagMacaddress + "와 일치하는 태그 데이터가 없습니다.");
		}
		long toolboxId = tag.getToolbox().getId();
		long toolId = tag.getTool().getId();
		
		List<Tag> list = repository.findAllByToolIdAndToolboxId(toolId, toolboxId);
		return list.stream().map(e->new TagDto(e)).toList();
	}

	@Transactional(readOnly=true)
	public Boolean isAvailable(String tag) {
        return repository.findByMacaddress(tag)==null;
	}
	
	
}
