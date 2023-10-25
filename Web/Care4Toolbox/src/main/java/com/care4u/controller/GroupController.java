package com.care4u.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/group")
@Controller
@RequiredArgsConstructor
public class GroupController {

	@Autowired
	private MainPartService mainPartService;
	
	@Autowired
	private SubPartService subPartService;
	
	@GetMapping(value = "/list")
	public String list(Model model){
		List<MainPartDto> mainPartDtoList = mainPartService.list();
		List<SubPartDto> subPartDtoList = subPartService.listByMainPartId(1);
		
		model.addAttribute("mainPartDtoList", mainPartDtoList);
		model.addAttribute("subPartDtoList", subPartDtoList);
		
		return "group/list";
	}
	
	
	
	
}
