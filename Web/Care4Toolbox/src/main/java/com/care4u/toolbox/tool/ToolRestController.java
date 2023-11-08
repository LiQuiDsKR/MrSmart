package com.care4u.toolbox.tool;

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

@RestController
public class ToolRestController {
	
	private static final Logger logger = Logger.getLogger(ToolRestController.class);
	
	@Autowired
	private ToolService toolService;
	
	@Autowired
	private SubGroupService subGroupService;
	
    @PostMapping(value="/tool/new")
    public ResponseEntity<String> newMembership(@Valid @RequestBody ToolFormDto toolFormDto){
    	try {
    		SubGroupDto subGroupDto = subGroupService.get(Long.parseLong(toolFormDto.getSubGroupDto()));
    		toolService.addNew(
    				ToolDto.builder()
    				.id(0)
    				.name(toolFormDto.getName())
    				.engName(toolFormDto.getEngName())
    				.code(toolFormDto.getCode())
    				.buyCode(toolFormDto.getBuyCode())
    				.subGroupDto(subGroupDto)
    				.spec(toolFormDto.getSpec())
    				.unit(toolFormDto.getUnit())
    				.price(toolFormDto.getPrice())
    				.replacementCycle(toolFormDto.getReplacementCycle())
    				.build()
    				);
    	}catch(IllegalStateException e) {
    		String response = "서버에서 받은 데이터:"+toolFormDto.toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	String response = "서버에서 받은 데이터:"+toolFormDto.toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    /**
     * 2023-10-25 paging용
     * @param membershipSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value="/tool/getpage")
    public ResponseEntity<Page<ToolDto>> getMembershipPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
            ){

    	logger.info("page=" + page + ", size=" + size);
    		
        Pageable pageable = PageRequest.of(page,size);
        Page<ToolDto> toolPage = toolService.list(pageable);
        
        for (ToolDto item : toolPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(toolPage);
    }
}