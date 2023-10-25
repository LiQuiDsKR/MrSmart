package com.care4u.toolbox.sheet.return_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnSheetService {

	private final Logger logger = LoggerFactory.getLogger(ReturnSheetService.class);
	
	private final ReturnSheetRepository repository;
	
	@Transactional(readOnly = true)
	public ReturnSheetDto get(long id){
		Optional<ReturnSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new ReturnSheetDto(item.get());
	}

}
