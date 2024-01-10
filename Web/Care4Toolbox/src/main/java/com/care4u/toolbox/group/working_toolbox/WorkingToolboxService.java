package com.care4u.toolbox.group.working_toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkingToolboxService {

	private final Logger logger = LoggerFactory.getLogger(WorkingToolboxService.class);
	
	private final WorkingToolboxRepository repository;
	
	@Transactional(readOnly = true)
	public WorkingToolboxDto get(long id){
		Optional<WorkingToolbox> item = repository.findById(id);
		if (item.isEmpty()) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new WorkingToolboxDto(item.get());
	}	
	
	@Transactional(readOnly = true)
	public WorkingToolboxDto get(String name){
		WorkingToolbox item = repository.findByName(name);
		if (item == null) {
			logger.error("Invalid name : " + name);
			return null;
		}
		
		return new WorkingToolboxDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<WorkingToolboxDto> list(){
		List<WorkingToolbox> list = repository.findAllByOrderByNameAsc();
		return getDtoList(list);
	}
	
	public WorkingToolboxDto addNew(WorkingToolboxDto workingToolDto) throws IllegalStateException {
		WorkingToolbox findItem = repository.findByName(workingToolDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + workingToolDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(workingToolDto);
	}
		
	public WorkingToolboxDto update(WorkingToolboxDto workingToolboxDto) throws IllegalStateException {				
		WorkingToolbox workingToolbox = repository.findByName(workingToolboxDto.getName());
		if (workingToolbox == null) {
			workingToolbox = new WorkingToolbox();
		}
		workingToolbox.update(workingToolboxDto);
		
		return new WorkingToolboxDto(repository.save(workingToolbox));
	}
	
	private List<WorkingToolboxDto> getDtoList(List<WorkingToolbox> list){
		List<WorkingToolboxDto> dtoList = new ArrayList<WorkingToolboxDto>();
		for (WorkingToolbox item : list) {
			dtoList.add(new WorkingToolboxDto(item));
			
		}
		return dtoList;
	}

}
