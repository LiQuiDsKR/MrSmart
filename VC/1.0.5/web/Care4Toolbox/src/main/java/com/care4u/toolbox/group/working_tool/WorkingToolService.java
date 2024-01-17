package com.care4u.toolbox.group.working_tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.group.working_toolbox.WorkingToolbox;
import com.care4u.toolbox.group.working_toolbox.WorkingToolboxRepository;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkingToolService {

	private final Logger logger = LoggerFactory.getLogger(WorkingToolService.class);
	
	private final WorkingToolRepository repository;
	private final WorkingToolboxRepository workingToolboxRepository;
	private final ToolRepository toolRepository;
	
	@Transactional(readOnly = true)
	public WorkingToolDto get(long id){
		Optional<WorkingTool> item = repository.findById(id);
		if (item.isEmpty()) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new WorkingToolDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public List<WorkingToolDto> list(long workingToolboxId){
		List<WorkingTool> list = repository.findAllByWorkingToolboxId(workingToolboxId);
		return getDtoList(list);
	}
	
	public WorkingToolDto addNew(long workingToolboxId, WorkingToolDto workingToolDto) throws IllegalStateException {
		
		WorkingTool findItem = repository.findByWorkingToolboxIdAndToolId(workingToolboxId, workingToolDto.getToolDto().getId());
		if(findItem != null){
			logger.error("이미 등옥된 작업용공기구입니다. : " + workingToolDto.getToolDto().getName());
			throw new IllegalStateException("이미 등옥된 작업용공기구입니다.");
		}
		
		return update(workingToolboxId, workingToolDto);
	}
		
	public WorkingToolDto update(long workingToolboxId, WorkingToolDto workingToolDto) throws IllegalStateException {
		Optional<WorkingToolbox> workingToolbox = workingToolboxRepository.findById(workingToolboxId);
		if (workingToolbox.isEmpty()) {
			logger.error("등록되지 않은 작업용공구함입니다. : " + workingToolDto.getWorkingToolboxDto().getName());
			throw new IllegalStateException("등록되지 않은 작업용공구함입니다.");
		}
		
		Optional<Tool> tool = toolRepository.findById(workingToolDto.getToolDto().getId());
		if (tool.isEmpty()) {
			logger.error("등록되지 않은 공기구입니다. : " + workingToolDto.getWorkingToolboxDto().getName());
			throw new IllegalStateException("등록되지 않은 공기구입니다.");
		}
		
		WorkingTool findItem = repository.findByWorkingToolboxIdAndToolId(workingToolboxId, workingToolDto.getToolDto().getId());
		if (findItem == null) {
			findItem = new WorkingTool();
		}
		findItem.update(workingToolbox.get(), tool.get(), workingToolDto.getCount());
		
		return new WorkingToolDto(repository.save(findItem));
	}
	
	private List<WorkingToolDto> getDtoList(List<WorkingTool> list){
		List<WorkingToolDto> dtoList = new ArrayList<WorkingToolDto>();
		for (WorkingTool item : list) {
			dtoList.add(new WorkingToolDto(item));
			
		}
		return dtoList;
	}

}
