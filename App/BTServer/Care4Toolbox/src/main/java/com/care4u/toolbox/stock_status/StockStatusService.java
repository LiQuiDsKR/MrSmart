package com.care4u.toolbox.stock_status;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StockStatusService {

	private final Logger logger = LoggerFactory.getLogger(StockStatusService.class);
	
	private final StockStatusRepository repository;
	
	@Transactional(readOnly = true)
	public StockStatusDto get(long id){
		Optional<StockStatus> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new StockStatusDto(item.get());
	}

}
