package com.care4u.controller;

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

import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;

@Controller
public class MainController {
	
	private static final Logger logger = Logger.getLogger(MainController.class);
	
	@Autowired
	private ToolService toolService;
	
	@Autowired
	private ToolboxService toolboxService;
	
	@GetMapping(value = "")
    public String toolState(Model model){
    	model.addAttribute("toolboxList",toolboxService.list());
        return "analytics/tool_states2";
    }
    
    @GetMapping(value = "/test")
    public String test(Model model) {
    	return "test";
    }
    
    /*
    
    @GetMapping(value = "/tool_list")
    public String getToolList(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model){
    	
    	Pageable pageable = PageRequest.of(page, size);
        Page<ToolDto> toolPage = toolService.list(pageable);

        model.addAttribute("toolPage", toolPage);
        
        return "tool_list";
    }
    
    @GetMapping(value = "/tool_list_ajax")
    public String getToolListAjax(Model model){        
        return "tool_list_ajax";
    }
    
    @GetMapping("/api/tool_list")
    public ResponseEntity<Page<ToolDto>> listTools(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "100") int size,
        @RequestParam(name = "search", required = false) String search) {
    	
    	logger.info("page=" + page + ", size=" + size);
    	
    	if (search != null) {
    		logger.info("search=" + search);
    	}
    	
        Pageable pageable = PageRequest.of(page, size);
        Page<ToolDto> toolPage = toolService.list(pageable);
        logger.info("tool total page : " + toolPage.getTotalPages() + ", current page : " + toolPage.getNumber());
        
        return ResponseEntity.ok(toolPage);
    }
    */
   
}