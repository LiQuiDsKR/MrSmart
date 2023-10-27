package com.care4u.toolbox.sheet.supply_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplyToolService {

	private final Logger logger = LoggerFactory.getLogger(SupplyToolService.class);
	
	private final SupplyToolRepository repository;
	
	@Transactional(readOnly = true)
	public SupplyToolDto get(long id){
		Optional<SupplyTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new SupplyToolDto(item.get());
	}

}
