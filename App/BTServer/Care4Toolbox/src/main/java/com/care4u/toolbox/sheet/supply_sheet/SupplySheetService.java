package com.care4u.toolbox.sheet.supply_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplySheetService {

	private final Logger logger = LoggerFactory.getLogger(SupplySheetService.class);
	
	private final SupplySheetRepository repository;
	
	@Transactional(readOnly = true)
	public SupplySheetDto get(long id){
		Optional<SupplySheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new SupplySheetDto(item.get());
	}

}
