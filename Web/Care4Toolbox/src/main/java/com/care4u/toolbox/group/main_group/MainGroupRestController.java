package com.care4u.toolbox.group.main_group;

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
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;

@RestController
public class MainGroupRestController {
	
	private static final Logger logger = Logger.getLogger(MainGroupRestController.class);
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private SubPartService subPartService;
	
    @PostMapping(value="/main_group/new")
    public ResponseEntity<String> newMainGroup(@Valid @RequestBody MainGroupFormDto mainGroupFormDto){
    	try {
    		mainGroupService.addNew(
    				MainGroupDto.builder()
    				.id(0)
    				.name(mainGroupFormDto.getName())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + mainGroupFormDto.getName()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + mainGroupFormDto.getName()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/main_group/edit")
    public ResponseEntity<String> editMainGroup(@Valid @RequestBody MainGroupFormDto mainGroupFormDto){
    	try {
    		mainGroupService.update(
    				MainGroupDto.builder()
    				.id(mainGroupFormDto.getId())
    				.name(mainGroupFormDto.getName())
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