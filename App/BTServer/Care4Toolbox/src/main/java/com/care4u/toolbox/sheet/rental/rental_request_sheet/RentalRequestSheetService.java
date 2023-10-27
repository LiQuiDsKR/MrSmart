package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalRequestSheetService {

	private final Logger logger = LoggerFactory.getLogger(RentalRequestSheetService.class);
	
	private final RentalRequestSheetRepository repository;
	
	@Transactional(readOnly = true)
	public RentalRequestSheetDto get(long id){
		Optional<RentalRequestSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalRequestSheetDto(item.get());
	}

}
