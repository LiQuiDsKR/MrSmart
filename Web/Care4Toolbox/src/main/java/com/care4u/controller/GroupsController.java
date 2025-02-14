package com.care4u.controller;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipController;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipSearchDto;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;

@Controller
@RequestMapping("/groups")
public class GroupsController {
	
	private static final Logger logger = Logger.getLogger(GroupsController.class);
	
	@Autowired
	private MainGroupService mainGroupService;

	@GetMapping(value = "")
	public String groupsPage(Model model){
		
		List<MainGroupDto> mainGroupList = mainGroupService.list();
		model.addAttribute("mainGroupList",mainGroupList);
		
		return "groups/groups";
	}
	
}
