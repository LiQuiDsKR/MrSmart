package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OutstandingRentalSheetService {

	private final Logger logger = LoggerFactory.getLogger(OutstandingRentalSheetService.class);
	
	private final OutstandingRentalSheetRepository repository;
	private final RentalToolService rentalToolService;
	
	@Transactional(readOnly = true)
	public OutstandingRentalSheetDto get(long id){
		Optional<OutstandingRentalSheet> item = repository.findById(id);
		if (item == null) {
			logger.error("Invalid id : " + id);
			return null;
		}
		return new OutstandingRentalSheetDto(item.get(),rentalToolService.list(item.get().getRentalSheet().getId()));
	}
	
	@Transactional
	public OutstandingRentalSheet addNew(RentalSheet sheet, List<RentalTool> toolList) {
		int totalCount=0;
		for (RentalTool tool : toolList) {
			totalCount+=tool.getCount();
		}
		OutstandingRentalSheet outstandingSheet = OutstandingRentalSheet.builder()
				.rentalSheet(sheet)
				.totalCount(totalCount)
				.totalOutstandingCount(totalCount)
				.build();
		
		return repository.save(outstandingSheet);
	}

}
