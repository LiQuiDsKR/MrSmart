package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalSheetService {

	private final Logger logger = LoggerFactory.getLogger(RentalSheetService.class);
	
	private final RentalSheetRepository repository;
	
	@Transactional(readOnly = true)
	public RentalSheetDto get(long id){
		Optional<RentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new RentalSheetDto(item.get());
	}

}
