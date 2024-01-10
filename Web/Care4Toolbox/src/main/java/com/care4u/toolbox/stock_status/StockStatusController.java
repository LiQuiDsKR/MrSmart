package com.care4u.toolbox.stock_status;

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
@RequestMapping("/analytics")
public class StockStatusController {
	
	private static final Logger logger = Logger.getLogger(StockStatusController.class);
	
	@Autowired
	private StockStatusService stockStatusSheetService;
	@Autowired
	private ToolboxService toolboxService;
	
//    @GetMapping(value = "tool_states")
//    public String toolState(Model model){
//    	model.addAttribute("toolboxList",toolboxService.list());
//        return "analytics/tool_states";
//    }
    
    @GetMapping(value = "tool_states/rental_return/old")
    public String rentalReturnStateOld(Model model){
    	model.addAttribute("toolboxList",toolboxService.list());
    	return "analytics/tool_states";
    } 
    @GetMapping(value = "tool_states/rental_return")
    public String rentalReturnState(Model model){
    	model.addAttribute("toolboxList",toolboxService.list());
    	return "analytics/tool_states2";
    }
    @GetMapping(value = "tool_states/main_group")
    public String mainGroupState(Model model){
    	model.addAttribute("toolboxList",toolboxService.list());
    	return "analytics/tool_states3";
    }
    @GetMapping(value = "other_stock")
    public String otherToolboxStock(Model model){
    	model.addAttribute("toolboxList",toolboxService.list());
    	return "analytics/other_stock";
    }
}