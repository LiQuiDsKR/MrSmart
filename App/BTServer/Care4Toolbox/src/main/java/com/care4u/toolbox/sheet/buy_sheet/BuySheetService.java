package com.care4u.toolbox.sheet.buy_sheet;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BuySheetService {

	private final Logger logger = LoggerFactory.getLogger(BuySheetService.class);
	
	private final BuySheetRepository repository;
	
	@Transactional(readOnly = true)
	public BuySheetDto get(long id){
		Optional<BuySheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new BuySheetDto(item.get());
	}

}
