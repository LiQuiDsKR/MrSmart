package com.care4u.toolbox.sheet.return_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnToolService {

	private final Logger logger = LoggerFactory.getLogger(ReturnToolService.class);
	
	private final ReturnToolRepository repository;
	
	@Transactional(readOnly = true)
	public ReturnToolDto get(long id){
		Optional<ReturnTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ReturnToolDto(item.get());
	}

}
