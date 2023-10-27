package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OutstandingRentalSheetService {

	private final Logger logger = LoggerFactory.getLogger(OutstandingRentalSheetService.class);
	
	private final OutstandingRentalSheetRepository repository;
	
	@Transactional(readOnly = true)
	public OutstandingRentalSheetDto get(long id){
		Optional<OutstandingRentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new OutstandingRentalSheetDto(item.get());
	}

}
