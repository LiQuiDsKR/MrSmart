package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

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
public class OutstandingRentalSheetController {
	
	private static final Logger logger = Logger.getLogger(OutstandingRentalSheetController.class);
	
	@Autowired
	private ToolboxService toolboxService;
	
	@Autowired
	private OutstandingRentalSheetService outstandingRentalSheetService;
	
    @GetMapping(value = "return/sheet_apply")
    public String createRequestSheet(Model model){
    	
    	List<ToolboxDto> toolboxList = toolboxService.list();
    	
    	model.addAttribute("toolboxList",toolboxList);

        return "return/sheet_apply";
    }
    
}