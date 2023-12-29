package com.care4u.toolbox.tool.supply_tool;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care4u.constant.ToolState;
import com.google.gson.Gson;

@RestController
public class SupplyToolRestController {
	
	private static final Logger logger = Logger.getLogger(SupplyToolRestController.class);
	
	@Autowired
	private SupplyToolService supplyToolService;


    @GetMapping(value="/supply/tool/getpage")
    public ResponseEntity<Page<SupplyToolDto>> getSupplyToolPage(
    		@RequestParam(name="partId") Long partId,
    		@RequestParam(name="membershipId") Long membershipId,
    		@RequestParam(name="toolId") Long toolId,
    		@RequestParam(name="isWorker") Boolean isWorker,
    		@RequestParam(name="isLeader") Boolean isLeader,
    		@RequestParam(name="isApprover") Boolean isApprover,
    		@RequestParam(name="page") int page,
    		@RequestParam(name="size") int size,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
            ){
    	logger.info("page=" + page + ", size=" + size);
    	
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    		
        Pageable pageable = PageRequest.of(page,size);
        
        Page<SupplyToolDto> supplyToolPage = supplyToolService.getPage(partId, membershipId, isWorker, isLeader, isApprover, toolId, startLocalDate, endLocalDate, pageable);
        
        for (SupplyToolDto item : supplyToolPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(supplyToolPage);
    }
}