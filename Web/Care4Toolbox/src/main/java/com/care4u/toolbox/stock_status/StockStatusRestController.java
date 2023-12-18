package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.care4u.toolbox.tool.ToolForRentalDto;
import com.care4u.toolbox.tool.ToolService;
import com.google.gson.Gson;

@RestController
public class StockStatusRestController {
	
	private static final Logger logger = Logger.getLogger(StockStatusRestController.class);
	
	@Autowired
	private ToolService toolService;

	@Autowired
	private StockStatusService stockStatusService;
	
	@Autowired
	private SubGroupService subGroupService;
	
    @GetMapping(value="/stock_status/get")
    public ResponseEntity<StockStatusDto> getStockStatus(
    		@RequestParam(name="toolId") Long toolId,
    		@RequestParam(name="toolboxId")Long toolboxId
    		){
    	StockStatusDto stockDto=stockStatusService.get(toolId,toolboxId);
    	return ResponseEntity.ok(stockDto);
    }
    
    @GetMapping(value="/stock_status/get/analytics")
    public ResponseEntity<StockStatusSummaryByToolStateDto> getStockStatusSummary(
    		@RequestParam(name="toolboxId") Long toolboxId,
    		@RequestParam(name="currentDate") String currentDate
    		){
    	
    	LocalDate currentLocalDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);
    	
    	StockStatusSummaryByToolStateDto summaryDto=stockStatusService.getSummary(toolboxId, currentLocalDate);
    	return ResponseEntity.ok(summaryDto);
    }
    @GetMapping(value="/stock_status/get/analytics/list")
    public ResponseEntity<List<StockStatusSummaryByToolStateDto>> getStockStatusSummaryList(
    		@RequestParam(name="toolboxId") Long toolboxId,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
    		){
    	
    	LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
    	LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    	
    	List<StockStatusSummaryByToolStateDto> summaryDto=stockStatusService.getSummary(toolboxId, startLocalDate, endLocalDate);
    	return ResponseEntity.ok(summaryDto);
    }
    @GetMapping(value="/stock_status/get/analytics/by_main_group")
    public ResponseEntity<List<StockStatusSummaryByMainGroupDto>> getStockStatusSummaryByMainGroup(
    		@RequestParam(name="toolboxId") Long toolboxId,
    		@RequestParam(name="currentDate") String currentDate
    		){
    	
    	LocalDate currentLocalDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE);
    	
    	List<StockStatusSummaryByMainGroupDto> summaryDto=stockStatusService.getStockStatusSummaryByMainGroupDto(toolboxId, currentLocalDate);
    	return ResponseEntity.ok(summaryDto);
    }
    
    @PostMapping(value="/stock_status/getpage")
    public ResponseEntity<Page<StockStatusDto>> getToolForRentalPage(@RequestBody StockStatusSearchDto data){

    	logger.info("page=" + data.page + ", size=" + data.size);
    	
    	List<Long> subGroupId;
    	if (data.getSubGroupId().isEmpty()) {
    		subGroupId=subGroupService.list().stream().map(SubGroupDto::getId).collect(Collectors.toList());
    	} else {
    		subGroupId=data.getSubGroupId();
    	}
    		
        Pageable pageable = PageRequest.of(data.page,data.size);
        Page<StockStatusDto> stockPage = stockStatusService.getTodayPage(data.toolboxId, data.name, subGroupId, pageable);
        
        for (StockStatusDto item : stockPage.getContent()) {
        	logger.info(item.toString());
        }
        return ResponseEntity.ok(stockPage);
    }
}