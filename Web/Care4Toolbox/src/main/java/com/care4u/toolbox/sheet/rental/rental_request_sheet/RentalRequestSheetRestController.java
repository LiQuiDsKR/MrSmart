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
import com.care4u.toolbox.tool.ToolDto;

@RestController
public class RentalRequestSheetRestController {
	
	private static final Logger logger = Logger.getLogger(RentalRequestSheetRestController.class);
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
    @PostMapping(value="/rental/request_sheet/apply")
    public ResponseEntity<String> newMembership(@Valid @RequestBody RentalRequestSheetFormDto rentalRequestSheetFormDto){
    	try {
    		//대충 서비스에다가 formdto 주고 add하는 내용
    	}catch(IllegalStateException e) {
    		String response = "aa";
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = "aa";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping(value="/rental/request_sheet/getpage")
    public ResponseEntity<Page<RentalRequestSheetDto>> getToolPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "toolboxId") long id
            ){

    	logger.info("page=" + page + ", size=" + size);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<RentalRequestSheetDto> rentalRequestSheetPage = rentalRequestSheetService.getRentalRequestSheetPageByToolboxId(pageable,id);
        
        for (RentalRequestSheetDto item : rentalRequestSheetPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(rentalRequestSheetPage);
    }

}