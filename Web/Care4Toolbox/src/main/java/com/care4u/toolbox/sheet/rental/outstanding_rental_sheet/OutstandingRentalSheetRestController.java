package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.SheetState;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetSearchDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.google.gson.Gson;

@RestController
public class OutstandingRentalSheetRestController {
	
	private static final Logger logger = Logger.getLogger(OutstandingRentalSheetRestController.class);
	
	@Autowired
	private OutstandingRentalSheetService outstandingRentalSheetService;
	
	@Autowired
	private RentalSheetService rentalSheetService;
	
	@GetMapping(value="/outstanding_rental_sheet/getpage")
    public ResponseEntity<Page<OutstandingRentalSheetDto>> getOutstandingRentalSheetPage(
    		@RequestParam(name="toolboxId") long toolboxId,
    		@RequestParam(name="page") int page,
    		@RequestParam(name="size") int size,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
            ){
    	logger.info("page=" + page + ", size=" + size);

        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<OutstandingRentalSheetDto> sheetPage = outstandingRentalSheetService.getPageByToolboxId(toolboxId, startLocalDate, endLocalDate, pageable);
        
        for (OutstandingRentalSheetDto item : sheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(sheetPage);
    }
	
	@GetMapping(value="/outstanding_rental_sheet/get_by_tag")
	public ResponseEntity<OutstandingRentalSheetDto> getOutstandingRentalSheetByTag(
			@RequestParam(name="macAddress") String macAddress
			){
		logger.info("tag=" + macAddress);
		OutstandingRentalSheetDto sheet = outstandingRentalSheetService.get(macAddress);
		logger.info(sheet.toString());
        return ResponseEntity.ok(sheet);
	}
}