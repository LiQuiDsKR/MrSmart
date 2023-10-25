package com.care4u.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;

@Controller
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
    
    @PostMapping(value="/membership_setting/new")
    public String updateMembership(@Valid MembershipDto memberFormDto, BindingResult bindingResult, Model model){
    	if(bindingResult.hasErrors()){
            return "setting/membership_setting";
        }
    	completeMembershipDto(memberFormDto);
    	try {
    		MembershipDto result = membershipService.update(memberFormDto.getPartDto().getId(), memberFormDto);
    	}catch(IllegalStateException e) {
    		model.addAttribute("errorMessage",e.getMessage());
    		return "setting/Membership_setting";
    	}
        return "setting/Membership_setting";
    }
    
    @GetMapping("/sub_parts")
    @ResponseBody
    public  List<SubPartDto> getSubPart(@RequestParam Long mainPartId) {
        // 메인 그룹 ID를 기반으로 서브 그룹 목록을 데이터베이스 또는 다른 소스에서 가져와서 반환합니다.
        List<SubPartDto> subPartList = subPartService.listByMainPartId(mainPartId);
        return subPartList;
    }
    
    @GetMapping("/parts")
    @ResponseBody
    public  List<PartDto> getPart(@RequestParam Long subPartId) {
        List<PartDto> partList = partService.listBySubPartId(subPartId);
        return partList;
    }
    
    /**
     * 임시 membershipDto의 빈칸 채워주는 메서드
     */
    private void completeMembershipDto(MembershipDto mem) {
    	List<MembershipDto> list = membershipService.list();
    	mem.setId((long)list.size()+1);
    	mem.setRole(Role.USER);
    	mem.setPartDto(partService.get(mem.getPartDto().getId()));
    	mem.setEmploymentState(EmploymentState.EMPLOYMENT);
    }
    
}