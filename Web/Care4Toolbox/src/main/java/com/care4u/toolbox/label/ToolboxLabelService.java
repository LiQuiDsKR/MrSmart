package com.care4u.toolbox.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ToolboxLabelService {

	private final Logger logger = LoggerFactory.getLogger(ToolboxLabelService.class);
	
	private final ToolboxLabelRepository repository;
	private final ToolboxRepository toolboxRepository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public ToolboxLabelDto get(long id){
		Optional<ToolboxLabel> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ToolboxLabelDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public ToolboxLabelDto get(ToolboxDto toolboxDto, String location){
		Toolbox toolbox = toolboxRepository.findByName(toolboxDto.getName());
		if (toolbox == null) {
			logger.error("Invalid toolboxDto : " + toolboxDto.getName());
			return null;
		}
		
		ToolboxLabel item = repository.findByToolboxAndLocation(toolbox, location);
		if (item == null) {
			logger.error("Invalid location : " + location);
			return null;
		}
		
		return new ToolboxLabelDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<ToolboxLabelDto> listByToolboxId(long toolboxId){
		List<ToolboxLabelDto> list = new ArrayList<ToolboxLabelDto>();
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolbox : " + toolboxId);
			return list;
		}
		List<ToolboxLabel> subGroupList = repository.findAllByToolboxOrderByLocationAsc(toolbox.get());
		return getDtoList(subGroupList);
	}
	
	public ToolboxLabelDto update(ToolboxLabelDto toolboxToolLabelDto) {
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxToolLabelDto.getToolboxDto().getId());
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolboxId : " + toolboxToolLabelDto.getToolboxDto().getId());
			return null;
		}
		
		Optional<Tool> tool = toolRepository.findById(toolboxToolLabelDto.getToolDto().getId());		
		if (tool.isEmpty()) {
			logger.error("Invalid toolId : " + toolboxToolLabelDto.getToolDto().getId());
			return null;
		}
		
		ToolboxLabel toolboxToolLabel = repository.findByToolboxAndLocation(toolbox.get(), toolboxToolLabelDto.getLocation());
		if (toolboxToolLabel == null) {
			toolboxToolLabel = new ToolboxLabel();
		}
		toolboxToolLabel.update(toolbox.get(), toolboxToolLabelDto.getLocation(), tool.get());
		
		return new ToolboxLabelDto(repository.save(toolboxToolLabel));
	}
	
	private List<ToolboxLabelDto> getDtoList(List<ToolboxLabel> list){
		List<ToolboxLabelDto> dtoList = new ArrayList<ToolboxLabelDto>();
		for (ToolboxLabel item : list) {
			dtoList.add(new ToolboxLabelDto(item));
			
		}
		return dtoList;
	}

}
