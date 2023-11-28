package com.care4u.toolbox.sheet.rental.rental_sheet;


import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.SheetState;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.google.gson.Gson;

@RestController
public class RentalSheetRestController {
	
	private static final Logger logger = Logger.getLogger(RentalSheetRestController.class);
	
	@Autowired
	private RentalSheetService rentalSheetService;

    @PostMapping(value="/rental/sheet/getpage")
    public ResponseEntity<Page<RentalSheetDto>> getRentalRequestSheetPage(
    		@RequestBody RentalSheetSearchDto searchDto
            ){
    	logger.info("page=" + searchDto.getPage() + ", size=" + searchDto.getSize());
    		
        Pageable pageable = PageRequest.of(searchDto.getPage(),searchDto.getSize());
        Page<RentalSheetDto> rentalSheetPage = rentalSheetService.getPage(searchDto.getMembershipId(), searchDto.getStartDate(), searchDto.getEndDate(), pageable);
        
        for (RentalSheetDto item : rentalSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalSheetPage);
    }
}