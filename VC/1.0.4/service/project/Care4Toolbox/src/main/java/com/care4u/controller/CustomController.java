package com.care4u.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.main_part.MainPartRepository;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;

@RestController
public class CustomController {
	
	private static final Logger logger = Logger.getLogger(CustomController.class);
	
	@Autowired
	private MainPartService mainPartServ;
	@Autowired
	private SubPartService subPartServ;
	@Autowired
	private PartService partServ;
	@Autowired
	private MainGroupService mainGroupServ;
	@Autowired
	private SubGroupService subGroupServ;
	@Autowired
	private ToolboxService toolboxServ;

    @GetMapping(value = "/list/main_part")
    public List<MainPartDto> mainPartList(){
    	return mainPartServ.list();
    }
    @GetMapping(value = "/list/sub_part")
    public List<SubPartDto> subPartList(){
    	return subPartServ.list();
    }
    @GetMapping(value = "/list/part")
    public List<PartDto> partList(){
    	return partServ.list();
    }
    @GetMapping(value = "/list/main_group")
    public List<MainGroupDto> mainGroupList(){
    	return mainGroupServ.list();
    }
    @GetMapping(value = "/list/sub_group")
    public List<SubGroupDto> subGroupList(){
    	return subGroupServ.list();
    }
    @GetMapping(value = "/list/toolbox")
    public List<ToolboxDto> toolboxList(){
    	return toolboxServ.list();
    }
}