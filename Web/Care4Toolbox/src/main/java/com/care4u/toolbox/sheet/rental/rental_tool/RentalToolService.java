package com.care4u.toolbox.sheet.rental.rental_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalToolService {

	private final Logger logger = LoggerFactory.getLogger(RentalToolService.class);
	
	private final RentalToolRepository repository;
	
	@Transactional(readOnly = true)
	public RentalToolDto get(long id){
		Optional<RentalTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalToolDto(item.get());
	}

}
