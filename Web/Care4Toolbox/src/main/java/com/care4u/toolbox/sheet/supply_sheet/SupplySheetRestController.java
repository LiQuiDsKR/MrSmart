package com.care4u.toolbox.sheet.supply_sheet;


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
public class SupplySheetRestController {
	
	private static final Logger logger = Logger.getLogger(SupplySheetRestController.class);
	
	@Autowired
	private RentalSheetService rentalSheetService;
	@Autowired
	private SupplySheetService supplySheetService;


    @GetMapping(value="/supply/sheet/getpage")
    public ResponseEntity<Page<SupplySheetDto>> getReturnSheetPage(
    		@RequestParam(name="partId") Long partId,
    		@RequestParam(name="membershipId") Long membershipId,
    		@RequestParam(name="toolId") Long toolId,
    		@RequestParam(name="isWorker") Boolean isWorker,
    		@RequestParam(name="isLeader") Boolean isLeader,
    		@RequestParam(name="isApprover") Boolean isApprover,
    		@RequestParam(name="page") int page,
    		@RequestParam(name="size") int size,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate,
    		@RequestParam(name="subGroupId") Long subGroupId
            ){
    	logger.info("page=" + page + ", size=" + size);
    	
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    		
        Pageable pageable = PageRequest.of(page,size);
        
        Page<SupplySheetDto> supplySheetPage = supplySheetService.getPage(partId, membershipId, isWorker, isLeader, isApprover, toolId, subGroupId, startLocalDate, endLocalDate, pageable);
        
        for (SupplySheetDto item : supplySheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(supplySheetPage);
    }
    
    @GetMapping(value="/supply/get_monitor")
	public ResponseEntity<List<supplySheetCountDto>> getMonitor(@RequestParam(name = "partId") Long partId,
			@RequestParam(name = "membershipId") Long membershipId, @RequestParam(name = "toolId") Long toolId,
			@RequestParam(name = "isWorker") Boolean isWorker, @RequestParam(name = "isLeader") Boolean isLeader,
			@RequestParam(name = "isApprover") Boolean isApprover, @RequestParam(name = "startDate") String startDate,
			@RequestParam(name = "endDate") String endDate, @RequestParam(name = "subGroupId") Long subGroupId) {

		LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
		LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

		List<supplySheetCountDto> supplySheetList = supplySheetService.getMonitor(partId, membershipId, isWorker, isLeader,
				isApprover, toolId, subGroupId, startLocalDate, endLocalDate);

		for (supplySheetCountDto item : supplySheetList) {
			logger.info(item.toString());
		}
		return ResponseEntity.ok(supplySheetList);
	}
}