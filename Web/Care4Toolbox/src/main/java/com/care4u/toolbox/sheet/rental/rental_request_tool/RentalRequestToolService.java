package com.care4u.toolbox.sheet.rental.rental_request_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalRequestToolService {

	private final Logger logger = LoggerFactory.getLogger(RentalRequestToolService.class);
	
	private final RentalRequestToolRepository repository;
	
	@Transactional(readOnly = true)
	public RentalRequestToolDto get(long id){
		Optional<RentalRequestTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalRequestToolDto(item.get());
	}

}
