package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	
	@Transactional(readOnly = true)
	public StockStatusDto get(long toolId, long toolboxId) {
		
		LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0));
		LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
		StockStatus status= repository.findByToolIdAndToolboxIdAndCurrentDayBetween(toolId, toolboxId, startDateTime, endDateTime);
		
		return new StockStatusDto(status);
	}

}
