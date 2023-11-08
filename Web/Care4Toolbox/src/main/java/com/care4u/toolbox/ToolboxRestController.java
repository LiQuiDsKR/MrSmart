package com.care4u.toolbox;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.Role;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;

@RestController
public class ToolboxRestController {
	
	private static final Logger logger = Logger.getLogger(ToolboxRestController.class);
	
	@Autowired
	private ToolboxService toolboxService;
	
	@Autowired
	private MembershipService membershipService;
	
	
    @PostMapping(value="/toolbox/new")
    public ResponseEntity<String> newToolbox(@Valid @RequestBody ToolboxFormDto toolboxFormDto){
    	try {
    		toolboxService.addNew(
    				ToolboxDto.builder()
    				.id(0)
    				.name(toolboxFormDto.getName())
    				.managerDto(membershipService.loadUserByCode(toolboxFormDto.getManagerDtoCode()))
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "aa";
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "aa";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/toolbox/edit")
    public ResponseEntity<String> editToolbox(@Valid @RequestBody ToolboxFormDto toolboxFormDto){
    	try {
    		toolboxService.update(
    				ToolboxDto.builder()
    				.id(toolboxFormDto.getId())
    				.name(toolboxFormDto.getName())
    				.managerDto(membershipService.loadUserByCode(toolboxFormDto.getManagerDtoCode()))
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "aa";
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "aa";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}