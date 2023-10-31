package com.care4u.hr.membership;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;

@Controller
@RequestMapping("/membership")
public class MembershipController {
	
	private static final Logger logger = Logger.getLogger(MembershipController.class);
	
	@Autowired
	private MembershipService membershipService;
	
	@Autowired
	private MainPartService mainPartService;
    
    /**
     * 2023-10-25 paging용
     * @param membershipSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value = "")
    public String itemManage(MembershipSearchDto membershipSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 30);
        Page<MembershipDto> membershipPage = membershipService.getMembershipPage(membershipSearchDto, pageable);
        
        for (MembershipDto item : membershipPage.getContent()) {
        	logger.info(item.toString());
        }
        
        
    	List<MainPartDto> mainPartDtoList = mainPartService.list();
    	MembershipFormDto memberFormDto = MembershipFormDto.builder()
    			.name(null)
    			.code(null)
    			.password(null)
    			.partDtoId(null)
    			.employmentStatus(null)
    			.build();
    	
        model.addAttribute("membershipPage", membershipPage); //memberships는 page객체입니다
        model.addAttribute("membershipSearchDto", membershipSearchDto);
        model.addAttribute("maxPage", 10); 
        
    	model.addAttribute("mainPartDtoList", mainPartDtoList);
    	
    	model.addAttribute("roles", Role.values());
    	model.addAttribute("employmentStates", EmploymentState.values());
    	
    	//아래 두 개는 form용
    	model.addAttribute("memberFormDto",memberFormDto);
    	//model.addAttribute("memberFormPartDtoId",memberFormPartDtoId); //memberFormDto로 합침

        return "membership/membership";
    }
    
   /*	
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
    
    */      
    
    /**
     * 23-10-29 박경수
     * 원래 있던거랑 이거랑 합침
     * @param model
     * @return
     */
    @GetMapping(value = "/new")
    public String newForm(Model model){
        model.addAttribute("membershipFormDto", MembershipFormDto.builder()
        		.name(null)
        		.code(null)
        		.password(null)
        		.partDtoId(null).build());
        return "membership/newForm";
    }

    @PostMapping(value = "/new")
    public String addNew(@Valid MembershipFormDto membershipFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "membership/newForm";
        }

        try {
            Membership item = Membership.builder()
            							.memberFormDto(membershipFormDto)
            							.build();
            membershipService.addNew(item);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "membership/newForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String login(){
        return "/membership/loginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/membership/loginForm";
    }
    
}