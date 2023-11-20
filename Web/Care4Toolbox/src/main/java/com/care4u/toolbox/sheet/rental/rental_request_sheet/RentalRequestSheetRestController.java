package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.util.ArrayList;
import java.util.List;

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

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;
import com.care4u.toolbox.tool.ToolDto;
import com.google.gson.Gson;

@RestController
public class RentalRequestSheetRestController {
	
	private static final Logger logger = Logger.getLogger(RentalRequestSheetRestController.class);
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
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
        Page<RentalRequestSheetDto> rentalRequestSheetPage = rentalRequestSheetService.getPage(toolboxId,pageable);
        
        for (RentalRequestSheetDto item : rentalRequestSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalRequestSheetPage);
    }
    
    @PostMapping(value="/rental/request_sheet/approve")
    public ResponseEntity<RentalSheetDto> approveRentalRequestSheet(
    		@Valid @RequestBody RentalRequestSheetDto sheetDto,
    		@RequestParam(name = "approverId") long approverId
    		){

		return null;
    }

}