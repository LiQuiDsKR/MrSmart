package com.care4u.toolbox.sheet.rental.rental_sheet;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value="/rental/sheet/getpage")
    public ResponseEntity<Page<RentalSheetDto>> getRentalSheetPage(
    		@RequestParam(name="membershipId") long membershipId,
    		@RequestParam(name="page") int page,
    		@RequestParam(name="size") int size,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
            ){
    	logger.info("page=" + page + ", size=" + size);
    	
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<RentalSheetDto> rentalSheetPage = rentalSheetService.getPage(membershipId, startLocalDate, endLocalDate, pageable);
        
        for (RentalSheetDto item : rentalSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalSheetPage);
    }
}