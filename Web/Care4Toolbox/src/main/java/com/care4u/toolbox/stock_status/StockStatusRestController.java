package com.care4u.toolbox.stock_status;

import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;

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
    		@RequestParam(name="partId") Long partId,
    		@RequestParam(name="membershipId") Long membershipId,
    		@RequestParam(name="toolId") Long toolId,
    		@RequestParam(name="toolboxId") Long toolboxId,
    		@RequestParam(name="isWorker") Boolean isWorker,
    		@RequestParam(name="isLeader") Boolean isLeader,
    		@RequestParam(name="isApprover") Boolean isApprover,
    		@RequestParam(name="startDate") String startDate,
    		@RequestParam(name="endDate") String endDate
    		){
    	
    	LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
    	LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    	
    	List<StockStatusSummaryByToolStateDto> summaryDto=stockStatusService.getSummary(partId, membershipId, toolId, toolboxId, isWorker, isLeader, isApprover, startLocalDate, endLocalDate);
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
    
    @PostMapping("/stock_status/initialize")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    	try (
    			InputStream inputStream = file.getInputStream();
    		     Workbook workbook = new XSSFWorkbook(inputStream)
    		){
    		
    		Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
    		List<Integer> columnIndices = new ArrayList();
	    	for (Row row : sheet) {
	    	    for (Cell cell : row) {
//	    	    	if (row.getRowNum()==0) {
//	    	    		switch(cell.getStringCellValue()) {
//	    	    		case 
//	    	    		}
//	    	    	}
	    	    	switch(cell.getCellType()) {
	    	    	case STRING:
	                    logger.debug(cell.getAddress()+": String value: " + cell.getStringCellValue());
	                    break;
	                case NUMERIC:
	                	logger.debug(cell.getAddress()+": Numeric value: " + cell.getNumericCellValue());
	                    break;
	                case BOOLEAN:
	                    logger.debug(cell.getAddress()+": Boolean value: " + cell.getBooleanCellValue());
	                    break;
	                case FORMULA:
	                    logger.debug(cell.getAddress()+": Formula: " + cell.getCellFormula());
	                    break;
	                case BLANK:
	                    logger.debug(cell.getAddress()+": Blank cell");
	                    break;
	                case ERROR:
	                    logger.debug(cell.getAddress()+": Error in cell");
	                    break;
	                default:
	                    logger.debug(cell.getAddress()+": Unknown cell type");
	                    break;
	    	    	}
	    	    }
	    	}
	    	
		} catch (Exception e) {
		    // 예외 처리 로직
		}
        return ResponseEntity.ok().body("{\"message\":\"["+file.getOriginalFilename()+"] upload complete\"}");
    }
}