package com.care4u.toolbox.sheet.rental.rental_request_sheet;


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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.common.GsonUtils;
import com.care4u.constant.SheetState;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.tag.TagDto;
import com.care4u.toolbox.tag.TagService;
import com.google.gson.Gson;

@RestController
public class RentalRequestSheetRestController {
	
	private static final Logger logger = Logger.getLogger(RentalRequestSheetRestController.class);
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
	@Autowired
	private RentalSheetService rentalSheetService;
	
	//작업자 저장
	@PostMapping(value="/rental/request_sheet/save")
    public ResponseEntity<String> saveRentalRequestSheet(@Valid @RequestBody RentalRequestSheetFormDto rentalRequestSheetFormDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
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
	//관리자 신청
    @PostMapping(value="/rental/request_sheet/apply")
    public ResponseEntity<String> applyRentalRequestSheet(@Valid @RequestBody RentalRequestSheetFormDto rentalRequestSheetFormDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
    	Gson gson = new Gson();
    	try {
    		rentalRequestSheetService.addNew(rentalRequestSheetFormDto,SheetState.REQUEST);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(rentalRequestSheetFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = gson.toJson(rentalRequestSheetFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping(value="/rental/request_sheet/getpage")
    public ResponseEntity<Page<RentalRequestSheetDto>> getRentalRequestSheetPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "toolboxId") long toolboxId,
            @RequestParam(name = "membershipId") long membershipId,
            @RequestParam(name="isWorker") Boolean isWorker,
    		@RequestParam(name="isLeader") Boolean isLeader,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
            ){

    	logger.info("page=" + page + ", size=" + size);
    	

        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    	
        Pageable pageable = PageRequest.of(page,size);
        Page<RentalRequestSheetDto> rentalRequestSheetPage = rentalRequestSheetService.getPage(SheetState.REQUEST, membershipId, isWorker, isLeader, toolboxId, startLocalDate, endLocalDate, pageable);
        
        for (RentalRequestSheetDto item : rentalRequestSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalRequestSheetPage);
    }
    
    //tag를 찍고 난 후 List<sheet>를 쿼리합니다.
    @GetMapping(value="/rental/request_sheet/list_by_tag")
    public ResponseEntity<List<RentalRequestSheetDto>> getRentalRequestSheetListByTag(
    		@RequestParam(name="tagMacAddress") String tagMacAddress
    		){
    	List<RentalRequestSheetDto> list = rentalRequestSheetService.listByTag(tagMacAddress);

    	logger.info("list size : " + list.size());
        for (RentalRequestSheetDto item : list) {
        	logger.info(item.toString());
        }
    	return ResponseEntity.ok(list);
    }
    
    @PostMapping(value="/rental/request_sheet/approve")
    public ResponseEntity<String> approveRentalRequestSheet(
    		@Valid @RequestBody RentalRequestSheetWithApproverIdDto sheetWithIdDto, BindingResult bindingResult){
    	if (bindingResult.hasErrors()) {
    		List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
    	}
    	RentalRequestSheetDto sheetDto = sheetWithIdDto.getRentalRequestSheetDto();
    	long approverId = sheetWithIdDto.getApproverId();
    	RentalSheetDto result=null;
    	String response = null;
    	Gson gson = new Gson();
        try {
            result = rentalSheetService.updateAndAddNewInTransaction(sheetDto, approverId);
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
        response = GsonUtils.toJson("{}");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value="/rental/request_sheet/cancel")
    public ResponseEntity<String> cancelRentalRequestSheet(
    		@RequestBody Long sheetId
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