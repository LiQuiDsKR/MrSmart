package com.care4u.controller;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.constant.SheetState;
import com.care4u.hr.main_part.MainPart;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipSearchDto;
import com.care4u.hr.part.Part;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.part.PartWrapperDto;
import com.care4u.hr.sub_part.SubPart;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetDto;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheetWithApproverIdDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetService;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetDto;
import com.google.gson.Gson;

@RestController
public class PartsRestController {
	
	private static final Logger logger = Logger.getLogger(PartsRestController.class);
	
	@Autowired
	private PartService partService;
	@Autowired
	private SubPartService subPartService;
	@Autowired
	private MainPartService mainPartService;
	
	
	@GetMapping(value="/parts/get")
	public ResponseEntity<PartWrapperDto> getParts(
    		@RequestParam(name="partId") Long partId
            ){
		PartWrapperDto dto=null;
		if (partService.get(partId)!=null) { dto = new PartWrapperDto(partService.get(partId)); }
		if (subPartService.get(partId)!=null) { dto = new PartWrapperDto(subPartService.get(partId)); }
		if (mainPartService.get(partId)!=null) { dto = new PartWrapperDto(mainPartService.get(partId)); }
        return ResponseEntity.ok(dto);
    }

	
	@GetMapping(value="/parts/getpage")
	public ResponseEntity<Page<PartWrapperDto>> getPartsPageByName(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "name", defaultValue = "") String name
            ){
    	logger.info("page=" + page + ", size=" + size);
    	
        Pageable pageable = PageRequest.of(page,size);
        List<PartDto> partList = partService.getByName(name);
        //List<SubPartDto> subPartList = subPartService.getByName(name);
        //List<MainPartDto> mainPartList = mainPartService.getByName(name);
        List<PartWrapperDto> partsList = new ArrayList<PartWrapperDto>();
//        for (MainPartDto p : mainPartList) {
//        	partsList.add(new PartWrapperDto(p));
//        }
//        for (SubPartDto p : subPartList) {
//        	partsList.add(new PartWrapperDto(p));
//        }
        for (PartDto p : partList) {
        	partsList.add(new PartWrapperDto(p));
        }
        
        
        PageRequest pageRequest = PageRequest.of(page, size);

		 // Calculate the sublist's start and end indices
		 int start = (int)pageRequest.getOffset();
		 int end = Math.min((start + pageRequest.getPageSize()), partsList.size());
		
		 // Create a sublist for the current page
		 List<PartWrapperDto> sublist = partsList.subList(start, end);
		
		 // Create a Page object
		 Page<PartWrapperDto> partsPage = new PageImpl<PartWrapperDto>(sublist, pageRequest, partsList.size());
        
        for (PartWrapperDto item : partsPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(partsPage);
    }
}