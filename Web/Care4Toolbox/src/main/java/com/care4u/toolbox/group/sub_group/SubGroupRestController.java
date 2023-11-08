package com.care4u.toolbox.group.sub_group;


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

import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;


@RestController
public class SubGroupRestController {
	
	private static final Logger logger = Logger.getLogger(SubGroupRestController.class);
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private SubGroupService subGroupService;
	
    @GetMapping("/sub_group/get")
    public List<SubGroupDto> getSubGroup(@RequestParam Long mainGroupId) {
        List<SubGroupDto> subGroupList = subGroupService.listByMainGroupId(mainGroupId);
        return subGroupList;
    }
    @PostMapping(value="/sub_group/new")
    public ResponseEntity<String> newSubGroup(@Valid @RequestBody SubGroupFormDto subGroupFormDto){
    	try {
    		MainGroupDto mainGroupDto = mainGroupService.get(subGroupFormDto.getMainGroupDtoId());
    		subGroupService.addNew(
    				subGroupFormDto.getMainGroupDtoId(),
    				SubGroupDto.builder()
    				.id(0)
    				.name(subGroupFormDto.getName())
    				.mainGroupDto(mainGroupDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + subGroupFormDto.getName()
		    		+ ", subgroupid=" + subGroupFormDto.getMainGroupDtoId()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + subGroupFormDto.getName()
	    		+ ", subgroupid=" + subGroupFormDto.getMainGroupDtoId()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value="/sub_group/edit")
    public ResponseEntity<String> editSubGroup(@Valid @RequestBody SubGroupFormDto subGroupFormDto){
    	try {
    		MainGroupDto mainGroupDto = mainGroupService.get(subGroupFormDto.getMainGroupDtoId());
    		subGroupService.update(
    				SubGroupDto.builder()
    				.id(subGroupFormDto.getId())
    				.name(subGroupFormDto.getName())
    				.mainGroupDto(mainGroupDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + subGroupFormDto.getName()
		    		+ ", subgroupid=" + subGroupFormDto.getMainGroupDtoId()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + subGroupFormDto.getName()
	    		+ ", subgroupid=" + subGroupFormDto.getMainGroupDtoId()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
}