package com.care4u.toolbox.sheet.return_sheet;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.SheetState;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetWithApproverIdDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.google.gson.Gson;

@RestController
public class ReturnSheetRestController {
	
	private static final Logger logger = Logger.getLogger(ReturnSheetRestController.class);
	
	@Autowired
	private RentalSheetService rentalSheetService;
	@Autowired
	private ReturnSheetService returnSheetService;


    @PostMapping(value="/return/sheet/approve")
    public ResponseEntity<String> approveReturnSheet(
    		@Valid @RequestBody ReturnSheetFormDto formDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
    	String response = null;
    	Gson gson = new Gson();
    	ReturnSheetDto result=null;
		try {
        	result = returnSheetService.addNew(formDto);
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
        response = gson.toJson(result.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
    @GetMapping(value="/return/sheet/getpage")
    public ResponseEntity<Page<ReturnSheetDto>> getReturnSheetPage(
    		@RequestParam(name="partId") Long partId,
    		@RequestParam(name="membershipId") Long membershipId,
    		@RequestParam(name="toolId") Long toolId,
    		@RequestParam(name="isWorker") Boolean isWorker,
    		@RequestParam(name="isLeader") Boolean isLeader,
    		@RequestParam(name="isApprover") Boolean isApprover,
    		@RequestParam(name="page") int page,
    		@RequestParam(name="size") int size,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
            ){
    	logger.info("page=" + page + ", size=" + size);
    	
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<ReturnSheetDto> returnSheetPage = returnSheetService.getPage(partId, membershipId, isWorker, isLeader, isApprover, toolId, startLocalDate, endLocalDate, pageable);
        
        for (ReturnSheetDto item : returnSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(returnSheetPage);
    }
}