package com.care4u.toolbox.sheet.rental.rental_request_sheet;


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
public class RentalRequestSheetRestController {
	
	private static final Logger logger = Logger.getLogger(RentalRequestSheetRestController.class);
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
	@Autowired
	private RentalSheetService rentalSheetService;
	
    @PostMapping(value="/rental/request_sheet/apply")
    public ResponseEntity<String> applyRentalRequestSheet(@Valid @RequestBody RentalRequestSheetFormDto rentalRequestSheetFormDto){
    	Gson gson = new Gson();
    	try {
    		rentalRequestSheetService.addNew(rentalRequestSheetFormDto);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(rentalRequestSheetFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = gson.toJson(rentalRequestSheetFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping(value="/rental/request_sheet/getpage")
    public ResponseEntity<Page<RentalRequestSheetDto>> getRentalRequestSheetPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "toolboxId") long toolboxId
            ){

    	logger.info("page=" + page + ", size=" + size);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<RentalRequestSheetDto> rentalRequestSheetPage = rentalRequestSheetService.getPage(SheetState.REQUEST,toolboxId,pageable);
        
        for (RentalRequestSheetDto item : rentalRequestSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalRequestSheetPage);
    }
    
    @PostMapping(value="/rental/request_sheet/approve")
    public ResponseEntity<String> approveRentalRequestSheet(
    		@Valid @RequestBody RentalRequestSheetWithApproverIdDto sheetWithIdDto
    		){
    	RentalRequestSheetDto sheetDto = sheetWithIdDto.getRentalRequestSheetDto();
    	long approverId = sheetWithIdDto.getApproverId();
    	RentalRequestSheetDto result1=null;
    	RentalSheetDto result2=null;
    	String response = null;
    	Gson gson = new Gson();
        try {
            result1 = rentalRequestSheetService.update(sheetDto,SheetState.APPROVE);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument: " + e.getMessage());
            response = gson.toJson(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        	response = gson.toJson(e.getMessage());
        } finally {
            try {
                result2 = rentalSheetService.addNew(sheetDto, approverId);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid argument: " + e.getMessage());
            	response = gson.toJson(e.getMessage());
            } catch (Exception e) {
                logger.error("An error occurred: " + e.getMessage());
            	response = gson.toJson(e.getMessage());
            }
        }
        if(response!=null) {
            return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
        }
        response = gson.toJson(result1.toString()+""+result2.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value="/rental/request_sheet/cancel")
    public ResponseEntity<String> cancelRentalRequestSheet(
    		@RequestParam(name = "sheetId") long sheetId
    		){
    	RentalRequestSheetDto result1=null;
    	String response = null;
    	Gson gson = new Gson();
        try {
            result1 = rentalRequestSheetService.cancel(sheetId);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument: " + e.getMessage());
            response = gson.toJson(e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        	response = gson.toJson(e.getMessage());
        }
        if(response!=null) {
            return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
        }
        response = gson.toJson(result1.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}