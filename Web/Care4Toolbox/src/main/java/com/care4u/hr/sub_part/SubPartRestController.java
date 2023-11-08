package com.care4u.hr.sub_part;


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

import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;

@RestController
public class SubPartRestController {
	
	private static final Logger logger = Logger.getLogger(SubPartRestController.class);
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
    @GetMapping("/sub_part/get")
    public List<SubPartDto> getSubPart(@RequestParam Long mainPartId) {
        List<SubPartDto> subPartList = subPartService.listByMainPartId(mainPartId);
        return subPartList;
    }
    @PostMapping(value="/sub_part/new")
    public ResponseEntity<String> newSubPart(@Valid @RequestBody SubPartFormDto subPartFormDto){
    	try {
    		MainPartDto mainPartDto = mainPartService.get(subPartFormDto.getMainPartDtoId());
    		subPartService.addNew(
    				subPartFormDto.getMainPartDtoId(),
    				SubPartDto.builder()
    				.id(0)
    				.name(subPartFormDto.getName())
    				.mainPartDto(mainPartDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + subPartFormDto.getName()
		    		+ ", subpartid=" + subPartFormDto.getMainPartDtoId()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + subPartFormDto.getName()
	    		+ ", subpartid=" + subPartFormDto.getMainPartDtoId()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value="/sub_part/edit")
    public ResponseEntity<String> editSubPart(@Valid @RequestBody SubPartFormDto subPartFormDto){
    	try {
    		MainPartDto mainPartDto = mainPartService.get(subPartFormDto.getMainPartDtoId());
    		subPartService.update(
    				SubPartDto.builder()
    				.id(subPartFormDto.getId())
    				.name(subPartFormDto.getName())
    				.mainPartDto(mainPartDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + subPartFormDto.getName()
		    		+ ", subpartid=" + subPartFormDto.getMainPartDtoId()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + subPartFormDto.getName()
	    		+ ", subpartid=" + subPartFormDto.getMainPartDtoId()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
}