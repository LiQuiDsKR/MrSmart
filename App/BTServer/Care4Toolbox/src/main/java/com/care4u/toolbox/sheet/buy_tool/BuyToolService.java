package com.care4u.toolbox.sheet.buy_tool;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BuyToolService {

	private final Logger logger = LoggerFactory.getLogger(BuyToolService.class);
	
	private final BuyToolRepository repository;
	
	@Transactional(readOnly = true)
	public BuyToolDto get(long id){
		Optional<BuyTool> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return new BuyToolDto(item.get());
	}

}
