package com.care4u.hr.part;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
import com.google.gson.Gson;

/**
 * 2023-11-02 박경수
 * fetch를 위해 만들었습니다
 */
@RestController
public class PartRestController {
	
	private static final Logger logger = Logger.getLogger(PartRestController.class);
	
	@Autowired
	private PartService partService;
	
	@Autowired
	private SubPartService subPartService;
	
   
    @GetMapping("/part/get")
    public List<PartDto> getPart(@RequestParam Long subPartId) {
        List<PartDto> partList = partService.listBySubPartId(subPartId);
        return partList;
    }
    @PostMapping(value="/part/new")
    public ResponseEntity<String> newPart(@Valid @RequestBody PartFormDto partFormDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
		}
		Gson gson = new Gson();
		try {
    		SubPartDto subPartDto = subPartService.get(partFormDto.getSubPartDtoId());
    		partService.addNew(
    				partFormDto.getSubPartDtoId(),
    				PartDto.builder()
    				.id(0)
    				.name(partFormDto.getName())
    				.subPartDto(subPartDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(partFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = gson.toJson(partFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/part/edit")
    public ResponseEntity<String> editMembership(@Valid @RequestBody PartFormDto partFormDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
		}
		Gson gson = new Gson();
    	try {
    		SubPartDto subPartDto = subPartService.get(partFormDto.getSubPartDtoId());
    		partService.update(
    				PartDto.builder()
    				.id(partFormDto.getId())
    				.name(partFormDto.getName())
    				.subPartDto(subPartDto)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(partFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = gson.toJson(partFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}