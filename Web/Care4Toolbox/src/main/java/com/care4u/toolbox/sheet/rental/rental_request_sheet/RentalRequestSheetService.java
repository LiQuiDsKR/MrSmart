package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalRequestSheetService {

	private final Logger logger = LoggerFactory.getLogger(RentalRequestSheetService.class);
	
	private final RentalRequestSheetRepository repository;
	
	private final RentalRequestToolRepository rentalRequestToolRepository;
	
	
	@Transactional(readOnly = true)
	public RentalRequestSheetDto get(long id){
		Optional<RentalRequestSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		
		return convertToDto(item.get());
	}
	
	@Transactional(readOnly = true)
	public Page<RentalRequestSheetDto> getRentalRequestSheetPageByToolboxId( Pageable pageable , Long toolboxId) {
		Page<RentalRequestSheet> page = repository.findAllByToolboxId(toolboxId, pageable);	
		
		
		return page.map(e->convertToDto(e));
	}
	
	private RentalRequestSheetDto convertToDto(RentalRequestSheet rentalRequestSheet) {
		List<RentalRequestTool> toolList = rentalRequestToolRepository.findAllByRentalRequestSheetId(rentalRequestSheet.getId());
		return new RentalRequestSheetDto(rentalRequestSheet,toolList);
	}

}
