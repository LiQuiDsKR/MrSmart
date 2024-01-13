package com.care4u.toolbox.sheet.rental.rental_request_sheet;

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
import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupService;

@Controller
@RequestMapping("/rental")
public class RentalRequestSheetController {
	
	private static final Logger logger = Logger.getLogger(RentalRequestSheetController.class);
	
	@Autowired
	private ToolboxService toolboxService;
	
	@Autowired
	private RentalRequestSheetService rentalRequestSheetService;
	
    @GetMapping(value = "request_sheet/manager/create")
    public String createRequestSheetManager(Model model){
    	
    	List<ToolboxDto> toolboxList = toolboxService.list();
    	
    	model.addAttribute("toolboxList",toolboxList);

        return "rental/request_sheet_create_manager";
    }
    @GetMapping(value = "request_sheet/user/create")
    public String createRequestSheetUser(Model model){
    	
    	List<ToolboxDto> toolboxList = toolboxService.list();
    	
    	model.addAttribute("toolboxList",toolboxList);

        return "rental/request_sheet_create_user";
    }
    
    @GetMapping(value = "request_sheet/approve")
    public String showRequestSheet(Model model){
    	return "rental/request_sheet_approve";
    }
    
}