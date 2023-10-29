package com.care4u.hr.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;

@RestController //rest
public class MembershipRestController {
	
	private static final Logger logger = Logger.getLogger(MembershipRestController.class);
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private SubGroupService subGroupService;
	
	@Autowired
	private MembershipService membershipService;
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
	@Autowired
	private PartService partService;
	
    

    @GetMapping("/setting/sub_parts")
    public List<SubPartDto> getSubPart(@RequestParam Long mainPartId) {
        List<SubPartDto> subPartList = subPartService.listByMainPartId(mainPartId);
        return subPartList;
    }
    @GetMapping("/setting/parts")
    public List<PartDto> getPart(@RequestParam Long subPartId) {
        List<PartDto> partList = partService.listBySubPartId(subPartId);
        return partList;
    }
    
	
  //Post
    @PostMapping(value="/setting/membership_setting2/new")
    public ResponseEntity<String> updateMembership2(@Valid @RequestBody MembershipFormDto memberFormDto){
    	try {
    		PartDto partDto=partService.get(Long.parseLong(memberFormDto.getPartDtoId()));
    		membershipService.addNew(
    				MembershipDto.builder()
    				.id(membershipService.getCount()+1)
    				.name(memberFormDto.getName())
    				.code(memberFormDto.getCode())
    				.password(memberFormDto.getPassword())
    				.partDto(partDto)
    				.role(Role.USER)
    				.employmentStatus(EmploymentState.EMPLOYMENT)
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"
    				+ "이름=" + memberFormDto.getName()
    				+ ", 코드=" + memberFormDto.getCode()
		    		+ ", pw=" + memberFormDto.getPassword()
		    		+ ", partid=" + memberFormDto.getPartDtoId()
		    		//+ ", empl=" + memberFormDto.getEmploymentState()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = "서버에서 받은 데이터:"
				+ "이름=" + memberFormDto.getName()
				+ ", 코드=" + memberFormDto.getCode()
	    		+ ", pw=" + memberFormDto.getPassword()
	    		+ ", partid=" + memberFormDto.getPartDtoId()
	    		//+ ", empl=" + memberFormDto.getEmploymentState()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }      
}