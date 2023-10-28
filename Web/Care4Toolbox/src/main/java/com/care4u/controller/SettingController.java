package com.care4u.controller;

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
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipSearchDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;

@Controller //rest
@RequestMapping("/setting")
public class SettingController {
	
	private static final Logger logger = Logger.getLogger(SettingController.class);
	
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
	
    @GetMapping(value = "/group_setting")
    public String getMainGroup(Model model){
    	List<MainGroupDto> mainGroupList = mainGroupService.list();
    	
    	
    	model.addAttribute("mainGroupList", mainGroupList);
    	
        return "setting/group_setting";
    }
    
    @GetMapping(value = "/list_subgroup")
    public List<SubGroupDto> getMainGroup(@RequestParam("mainGroupId") long mainGroupId){
    	//List<SubGroupDto> subGroupList = subGroupService.listByMainGroupId(mainGroupId);
    	List<SubGroupDto> subGroupList = new ArrayList<SubGroupDto>();
    	
        return subGroupList;
    }
    
    @PostMapping(value = "/group_setting/set")
    public String updateMainGroup(@Valid MainGroupDto mainGroupDto, BindingResult bindingResult, Model model){
    	if(bindingResult.hasErrors()){
            return "membership/newForm";
        }
    	
    	
    	MainGroupDto mainGroupList = mainGroupService.update(mainGroupDto);
    	
    	model.addAttribute("mainGroupList", mainGroupList);
    	
        return "setting/group_setting";
    }
    
    @GetMapping(value = "/membership_setting")
    public String getMembershipList(Model model) {
    	
    	List<MembershipDto> membershipDtoList = membershipService.list();
    	List<MainPartDto> mainPartDtoList = mainPartService.list();
    	
    	MembershipDto memberFormDto = MembershipDto.builder()
    			.id(0)
    			.name(null)
    			.code(null)
    			.password(null)
    			.partDto(null)
    			.role(Role.USER)
    			.employmentState(EmploymentState.EMPLOYMENT)
    			.build();
    			
    	
    	model.addAttribute("membershipDtoList",membershipDtoList);
    	model.addAttribute("memberFormDto",memberFormDto);
    	model.addAttribute("mainPartDtoList", mainPartDtoList);
    	
    	return "setting/member_setting";
    }
    
    //Post
    @PostMapping(value="/membership_setting/new")
    public String updateMembership(@RequestParam("memberFormPartDtoId") long memberFormPartDtoId, @Valid MembershipDto memberFormDto, BindingResult bindingResult, Model model){
    	if(bindingResult.hasErrors()){
            return "setting/membership_setting";
        }
    	try {
    		PartDto partDto=partService.get(memberFormPartDtoId);
    		memberFormDto.setPartDto(partDto);
    	}catch(IllegalStateException e) {
    		model.addAttribute("errorMessage",e.getMessage());
    		return "setting/Membership_setting";
    	}
        return "setting/Membership_setting";
    }
    
    
    //fetch용 메서드 : subpart
    @GetMapping("/sub_parts")
    @ResponseBody
    public List<SubPartDto> getSubPart(@RequestParam Long mainPartId) {
        // 메인 그룹 ID를 기반으로 서브 그룹 목록을 데이터베이스 또는 다른 소스에서 가져와서 반환합니다.
        List<SubPartDto> subPartList = subPartService.listByMainPartId(mainPartId);
        return subPartList;
    }
    
    //fetch용 메서드 : part
    @GetMapping("/parts")
    @ResponseBody
    public List<PartDto> getPart(@RequestParam Long subPartId) {
        List<PartDto> partList = partService.listBySubPartId(subPartId);
        return partList;
    }
    
    /**
     * 2023-10-25 paging용
     * @param membershipSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value = {"membership_setting2", "membership_setting2/{page}"})
    public String itemManage(MembershipSearchDto membershipSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 30);
        Page<Membership> memberships = membershipService.getMembershipPage(membershipSearchDto, pageable);
        
    	List<MainPartDto> mainPartDtoList = mainPartService.list();
    	MembershipFormDto memberFormDto = MembershipFormDto.builder()
    			.name(null)
    			.code(null)
    			.password(null)
    			.partDtoId(null)
    			//.employmentState(null)
    			.build();
    	long memberFormPartDtoId=0;
    	
        model.addAttribute("memberships", memberships); //memberships는 page객체입니다
        model.addAttribute("membershipSearchDto", membershipSearchDto);
        model.addAttribute("maxPage", 10); 
        
    	model.addAttribute("mainPartDtoList", mainPartDtoList);
    	
    	model.addAttribute("roles", Role.values());
    	model.addAttribute("employmentStates", EmploymentState.values());
    	
    	//아래 두 개는 form용
    	model.addAttribute("memberFormDto",memberFormDto);
    	//model.addAttribute("memberFormPartDtoId",memberFormPartDtoId); //memberFormDto로 합침

        return "setting/member_setting2";
    }
   /*
  //Post
    @PostMapping(value="/setting/membership_setting2/new")
    @ResponseBody
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
    				.employmentState(EmploymentState.EMPLOYMENT)
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
    */      
}