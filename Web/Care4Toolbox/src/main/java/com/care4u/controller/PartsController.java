package com.care4u.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.membership.MembershipController;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipSearchDto;
import com.care4u.hr.part.PartWrapperDto;
import com.care4u.toolbox.sheet.supply_sheet.SupplySheetDto;

@Controller
@RequestMapping("/parts")
public class PartsController {
	
	private static final Logger logger = Logger.getLogger(PartsController.class);
	
	@Autowired
	private MainPartService mainPartService;

	@GetMapping(value = "")
	public String partsPage(Model model){
		
		List<MainPartDto> mainPartList = mainPartService.list();
		model.addAttribute("mainPartList",mainPartList);
		
		return "parts/parts";
	}
	
}
