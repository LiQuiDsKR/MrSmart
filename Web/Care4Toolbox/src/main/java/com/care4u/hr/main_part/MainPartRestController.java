package com.care4u.hr.main_part;

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

@RestController
public class MainPartRestController {
	
	private static final Logger logger = Logger.getLogger(MainPartRestController.class);
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
    @PostMapping(value="/main_part/new")
    public ResponseEntity<String> newMainPart(@Valid @RequestBody MainPartFormDto mainPartFormDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
		}
    	
    	Gson gson = new Gson();
    	try {
    		mainPartService.addNew(
    				MainPartDto.builder()
    				.id(0)
    				.name(mainPartFormDto.getName())
    				//.latitude(mainPartFormDto.getLatitude())
    				//.longitude(mainPartFormDto.getLongitude())
    				//.mapScale(mainPartFormDto.getMapScale())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(mainPartFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = gson.toJson(mainPartFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/main_part/edit")
    public ResponseEntity<String> editMainPart(@Valid @RequestBody MainPartFormDto mainPartFormDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			return ResponseEntity.badRequest().body(String.join(" / ", errors));
		}
    	Gson gson = new Gson();

    	try {
    		mainPartService.update(
    				MainPartDto.builder()
    				.id(mainPartFormDto.getId())
    				.name(mainPartFormDto.getName())
    				//.latitude(mainPartFormDto.getLatitude())
    				//.longitude(mainPartFormDto.getLongitude())
    				//.mapScale(mainPartFormDto.getMapScale())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = gson.toJson(mainPartFormDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = gson.toJson(mainPartFormDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}