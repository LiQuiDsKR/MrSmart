package com.care4u.toolbox.tag;

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
public class TagService {

	private final Logger logger = LoggerFactory.getLogger(TagService.class);
	
	private final TagRepository repository;
	
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
	
	public TagDto update(TagDto tagDto) {		
		return null;
	}
	
	private List<TagDto> getDtoList(List<Tag> list){
		List<TagDto> dtoList = new ArrayList<TagDto>();
		for (Tag item : list) {
			dtoList.add(new TagDto(item));
			
		}
		return dtoList;
	}

}
