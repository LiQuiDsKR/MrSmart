package com.care4u.hr.membership;

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

@RestController
public class MembershipRestController {
	
	private static final Logger logger = Logger.getLogger(MembershipRestController.class);
	
	@Autowired
	private MembershipService membershipService;
	
	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
	@Autowired
	private PartService partService;
    
    @PostMapping(value="/membership/new")
    public ResponseEntity<String> newMembership(@Valid @RequestBody MembershipFormDto memberFormDto){
    	try {
    		PartDto partDto=partService.get(Long.parseLong(memberFormDto.getPartDtoId()));
    		membershipService.addNew(
    				MembershipDto.builder()
    				.id(0)
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
		    		+ ", empl=" + memberFormDto.getEmploymentStatus()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = "서버에서 받은 데이터:"
				+ "이름=" + memberFormDto.getName()
				+ ", 코드=" + memberFormDto.getCode()
	    		+ ", pw=" + memberFormDto.getPassword()
	    		+ ", partid=" + memberFormDto.getPartDtoId()
	    		+ ", empl=" + memberFormDto.getEmploymentStatus()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value="/membership/edit")
    public ResponseEntity<String> editMembership(@Valid @RequestBody MembershipFormDto memberFormDto){
    	try {
    		PartDto partDto=partService.get(Long.parseLong(memberFormDto.getPartDtoId()));
    		membershipService.update(
    				partDto.getId(), //
    				MembershipDto.builder()
    				.id(0)
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
		    		+ ", empl=" + memberFormDto.getEmploymentStatus()
		    		;
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = "서버에서 받은 데이터:"
				+ "이름=" + memberFormDto.getName()
				+ ", 코드=" + memberFormDto.getCode()
	    		+ ", pw=" + memberFormDto.getPassword()
	    		+ ", partid=" + memberFormDto.getPartDtoId()
	    		+ ", empl=" + memberFormDto.getEmploymentStatus()
	    		;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    /**
     * 2023-10-25 paging용
     * @param membershipSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value="/membership/getpage")
    public ResponseEntity<Page<MembershipDto>> getMembershipPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchBy", defaultValue = "null") String searchBy,
            @RequestParam(name = "role" , defaultValue = "USER") Role role,
            @RequestParam(name = "employmentStatus" , defaultValue= "EMPLOYMENT") EmploymentState employmentStatus,
            @RequestParam(name = "mainPartId", defaultValue="0") Long mainPartId,
            @RequestParam(name = "subPartId", required=false) Long subPartId,
            @RequestParam(name = "partId", required= false) Long partId,
            @RequestParam(name = "search", required = false) String searchQuery
            ){

    	
    	
    	logger.info("page=" + page + ", size=" + size);
    	if (searchQuery != null) {
    		logger.info("search=" + searchQuery);
    	}
    	
    	List<Long> partIds = new ArrayList<>();
    	if (partId!=null && partService.get(partId)!=null) {
    		partIds.add(partId);
    	}else if (subPartId!=null && subPartService.get(subPartId)!=null) {
    		for (PartDto partDto : partService.listBySubPartId(subPartId))
    		partIds.add(partDto.getId());
    	}else if (mainPartId!=null && mainPartService.get(mainPartId)!=null) {
    		for (SubPartDto subPartDto : subPartService.listByMainPartId(mainPartId))
    		partIds.add(subPartDto.getId());
    	}else {
    		partIds.clear();
    	}
    	
    	MembershipSearchDto membershipSearchDto = MembershipSearchDto.builder()
    			.ids(partIds)
    			.searchRole(role)
    			.searchEmploymentStatus(employmentStatus)
    			.searchBy(searchBy)
    			.searchQuery(searchQuery)
    			.build();
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<MembershipDto> membershipPage = membershipService.getMembershipPage(membershipSearchDto, pageable);
        
        for (MembershipDto item : membershipPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(membershipPage);
    }
}