package com.care4u.toolbox.group.main_group;

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
public class MainGroupService {

	private final Logger logger = LoggerFactory.getLogger(MainGroupService.class);
	
	private final MainGroupRepository repository;

	@Transactional(readOnly = true)
	public MainGroupDto get(long id){
		Optional<MainGroup> item = repository.findById(id);
		if (item.isEmpty()) {
			logger.error("Invalid id : " + id);
			return null;
		}
		return new MainGroupDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public MainGroupDto get(String name){
		MainGroup item = repository.findByName(name);
		if (item == null) {
			logger.error("Invalid name : " + name);
			return null;
		}
		return new MainGroupDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<MainGroupDto> list(){
		List<MainGroup> list = repository.findAllByOrderByNameAsc();
		
		List<MainGroupDto> dtoList = new ArrayList<MainGroupDto>();
		for (MainGroup item : list) {
			dtoList.add(new MainGroupDto(item));
			
		}
		return dtoList;
	}
	
	public MainGroupDto addNew(MainGroupDto mainGroupDto) throws IllegalStateException {
		MainGroup findItem = repository.findByName(mainGroupDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + mainGroupDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(mainGroupDto);
	}
	
	public MainGroupDto update(MainGroupDto mainGroupDto) {
		MainGroup mainGroup = repository.findByName(mainGroupDto.getName());
		if (mainGroup == null) {
			mainGroup = new MainGroup();
		}
		mainGroup.update(mainGroupDto.getName());
		
		return new MainGroupDto(repository.save(mainGroup));
	}
}
