package com.care4u.toolbox.sheet.supply_sheet;

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
@RequestMapping("/supply")
public class SupplySheetController {
	
	private static final Logger logger = Logger.getLogger(SupplySheetController.class);
	
	@Autowired
	private ToolboxService toolboxService;
	
    @GetMapping(value = "sheet")
    public String supplySheet(Model model) {
    	return "supply/sheet";
    }
    @GetMapping(value = "sheet/tool")
    public String supplyTool(Model model) {
    	return "supply/sheet_tool";
    }
    
}