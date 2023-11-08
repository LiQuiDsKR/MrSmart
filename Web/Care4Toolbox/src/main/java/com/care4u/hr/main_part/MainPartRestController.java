package com.care4u.hr.main_part;

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
public class MainPartRestController {
	
	private static final Logger logger = Logger.getLogger(MainPartRestController.class);
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
    @PostMapping(value="/main_part/new")
    public ResponseEntity<String> newMainPart(@Valid @RequestBody MainPartFormDto mainPartFormDto){
    	try {
    		mainPartService.addNew(
    				MainPartDto.builder()
    				.id(0)
    				.name(mainPartFormDto.getName())
    				.latitude(mainPartFormDto.getLatitude())
    				.longitude(mainPartFormDto.getLongitude())
    				.mapScale(mainPartFormDto.getMapScale())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + mainPartFormDto.getName()
		    		+ ", latitude=" + mainPartFormDto.getLatitude()
		    		+ ", longitude=" + mainPartFormDto.getLongitude()
		    		+ ", mapScale=" + mainPartFormDto.getMapScale()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + mainPartFormDto.getName()
	    		+ ", latitude=" + mainPartFormDto.getLatitude()
	    		+ ", longitude=" + mainPartFormDto.getLongitude()
	    		+ ", mapScale=" + mainPartFormDto.getMapScale()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/main_part/edit")
    public ResponseEntity<String> editMainPart(@Valid @RequestBody MainPartFormDto mainPartFormDto){
    	try {
    		mainPartService.update(
    				MainPartDto.builder()
    				.id(mainPartFormDto.getId())
    				.name(mainPartFormDto.getName())
    				.latitude(mainPartFormDto.getLatitude())
    				.longitude(mainPartFormDto.getLongitude())
    				.mapScale(mainPartFormDto.getMapScale())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + mainPartFormDto.getName()
		    		+ ", latitude=" + mainPartFormDto.getLatitude()
		    		+ ", longitude=" + mainPartFormDto.getLongitude()
		    		+ ", mapScale=" + mainPartFormDto.getMapScale()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
		String response = "서버에서 받은 데이터:"
				+ "이름=" + mainPartFormDto.getName()
	    		+ ", latitude=" + mainPartFormDto.getLatitude()
	    		+ ", longitude=" + mainPartFormDto.getLongitude()
	    		+ ", mapScale=" + mainPartFormDto.getMapScale()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}