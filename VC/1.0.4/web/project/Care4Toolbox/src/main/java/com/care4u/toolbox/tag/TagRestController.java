package com.care4u.toolbox.tag;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.main_part.MainPartService;
import com.care4u.hr.part.PartDto;
import com.care4u.hr.part.PartService;
import com.care4u.hr.sub_part.SubPartDto;
import com.care4u.hr.sub_part.SubPartService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.tool.ToolService;
import com.google.gson.Gson;

@RestController
public class TagRestController {
	
	private static final Logger logger = Logger.getLogger(TagRestController.class);
	
	@Autowired
	private ToolService toolService;

	@Autowired
	private TagService tagService;
	
    @GetMapping(value="/tag/get")
    public ResponseEntity<TagDto> getTag(
    		@RequestParam(name="macAddress") String macAddress
    		){
    	TagDto tagDto=tagService.get(macAddress);
    	return ResponseEntity.ok(tagDto);
    }
}