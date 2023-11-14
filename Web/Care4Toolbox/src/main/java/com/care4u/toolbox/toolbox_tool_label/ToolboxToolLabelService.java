package com.care4u.toolbox.toolbox_tool_label;

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
public class ToolboxToolLabelService {

	private final Logger logger = LoggerFactory.getLogger(ToolboxToolLabelService.class);
	
	private final ToolboxToolLabelRepository repository;
	private final ToolboxRepository toolboxRepository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(long id){
		Optional<ToolboxToolLabel> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ToolboxToolLabelDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(ToolboxDto toolboxDto, String location){
		Toolbox toolbox = toolboxRepository.findByName(toolboxDto.getName());
		if (toolbox == null) {
			logger.error("Invalid toolboxDto : " + toolboxDto.getName());
			return null;
		}
		
		ToolboxToolLabel item = repository.findByToolboxAndLocation(toolbox, location);
		if (item == null) {
			logger.error("Invalid location : " + location);
			return null;
		}
		
		return new ToolboxToolLabelDto(item);
	}
	
	@Transactional(readOnly = true)
	public ToolboxToolLabelDto get(long toolId, long toolboxId){
		ToolboxToolLabel label = repository.findByToolIdAndToolboxId(toolId, toolboxId);
		return new ToolboxToolLabelDto(label);
	}
	
	@Transactional(readOnly = true)
	public List<ToolboxToolLabelDto> listByToolboxId(long toolboxId){
		List<ToolboxToolLabelDto> list = new ArrayList<ToolboxToolLabelDto>();
		Optional<Toolbox> toolbox = toolboxRepository.findById(toolboxId);
		if (toolbox.isEmpty()) {
			logger.error("Invalid toolbox : " + toolboxId);
			return list;
		}
		List<ToolboxToolLabel> subGroupList = repository.findAllByToolboxOrderByLocationAsc(toolbox.get());
		return getDtoList(subGroupList);
	}
	
	public ToolboxToolLabelDto update(ToolboxToolLabelDto toolboxToolLabelDto) {
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
		
		ToolboxToolLabel toolboxToolLabel = repository.findByToolboxAndLocation(toolbox.get(), toolboxToolLabelDto.getLocation());
		if (toolboxToolLabel == null) {
			toolboxToolLabel = new ToolboxToolLabel();
		}
		toolboxToolLabel.update(toolbox.get(), toolboxToolLabelDto.getLocation(), tool.get(), toolboxToolLabelDto.getQrcode());
		
		return new ToolboxToolLabelDto(repository.save(toolboxToolLabel));
	}
	
	private List<ToolboxToolLabelDto> getDtoList(List<ToolboxToolLabel> list){
		List<ToolboxToolLabelDto> dtoList = new ArrayList<ToolboxToolLabelDto>();
		for (ToolboxToolLabel item : list) {
			dtoList.add(new ToolboxToolLabelDto(item));
			
		}
		return dtoList;
	}

}
