package com.care4u.hr.main_part;

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
public class MainPartService {

	private final Logger logger = LoggerFactory.getLogger(MainPartService.class);
	
	private final MainPartRepository repository;

	@Transactional(readOnly = true)
	public MainPartDto get(long id){
		Optional<MainPart> item = repository.findById(id);
		if (item.isEmpty()) {
			logger.error("Invalid id : " + id);
			return null;
		}
		return new MainPartDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public MainPartDto get(String name){
		MainPart item = repository.findByName(name);
		if (item == null) {
			logger.error("Invalid name : " + name);
			return null;
		}
		
		return new MainPartDto(item);
	}
	
	@Transactional(readOnly = true)
	public List<MainPartDto> list(){
		List<MainPart> list = repository.findAllByOrderByNameAsc();
		
		List<MainPartDto> dtoList = new ArrayList<MainPartDto>();
		for (MainPart item : list) {
			dtoList.add(new MainPartDto(item));			
		}
		return dtoList;
	}
	
	public MainPartDto addNew(MainPartDto mainPartDto) throws IllegalStateException {
		MainPart findItem = repository.findByName(mainPartDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + mainPartDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		return update(mainPartDto);
	}
	
	public MainPartDto update(MainPartDto mainPartDto) {
		MainPart findItem = repository.findByName(mainPartDto.getName());
		if(findItem != null){
			logger.error("이미 등옥된 이름입니다. : " + mainPartDto.getName());
			throw new IllegalStateException("이미 등옥된 이름입니다.");
		}
		
		MainPart mainPart;
		Optional<MainPart> optionalMainPart = repository.findById(mainPartDto.getId());
		if (optionalMainPart.isEmpty()) {
			mainPart = new MainPart(mainPartDto.getName());
		} else {
			mainPart = optionalMainPart.get();
		}
		
		mainPart.update(mainPartDto);
		
		return new MainPartDto(repository.save(mainPart));
	}
}
