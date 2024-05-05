package com.care4u.toolbox.stock_status;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.poi.ss.usermodel.CellType;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
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
	
	@Autowired
	private MainGroupService mainGroupService;
	
	@Autowired
	private ToolboxService toolboxService;
	
	
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
    public ResponseEntity<List<String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
    	try (
    			InputStream inputStream = file.getInputStream();
    		     Workbook workbook = new XSSFWorkbook(inputStream)
    		){
    		
    		int logCount = 1;
    		List<String> results = new ArrayList<String> ();
			Map<String, Integer> resultMap = new HashMap<>();
    		Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
    		Map<Integer, Integer> columnIndices = new HashMap<Integer,Integer>();
	    	for (Row row : sheet) {
	    		String mainGroup = null;
	    		String subGroup = null;
	    		String name = null;
	    		String engName=null;
	    		String code = null;
	    		String spec = null;
	    		String unit = null;
	    		String toolbox = null;
	    		Integer count = null;
	    		boolean exceptionFlag= false;
	    	    for (Cell cell : row) {
	    	    	if (row.getRowNum()==0) {
	    	    		switch(cell.getStringCellValue()) {
	    	    		case "대분류" :
	    	    			columnIndices.put(cell.getColumnIndex(),0);
	    	    			break;
	    	    		case "중분류" :
	    	    			columnIndices.put(cell.getColumnIndex(),1);
	    	    			break;
	    	    		case "한글명" :
	    	    			columnIndices.put(cell.getColumnIndex(),2);
	    	    			break;
	    	    		case "영문명" :
	    	    			columnIndices.put(cell.getColumnIndex(),3);
	    	    			break;
	    	    		case "품목코드" :
	    	    		case "코드":
	    	    			columnIndices.put(cell.getColumnIndex(),4);
	    	    			break;
	    	    		case "규격" :
	    	    			columnIndices.put(cell.getColumnIndex(),5);
	    	    			break;
	    	    		case "단위" :
	    	    			columnIndices.put(cell.getColumnIndex(),6);
	    	    			break;
	    	    		case "정비실" :
	    	    		case "부서명" :
	    	    			columnIndices.put(cell.getColumnIndex(),7);
	    	    			break;
	    	    		case "수량" :
	    	    			columnIndices.put(cell.getColumnIndex(),8);
	    	    			break;	    	    			
	    	    		}
	    	    	} else if (columnIndices.containsKey(cell.getColumnIndex())) {
	    	    		try {
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
	    	    		
	    	    		switch (columnIndices.get(cell.getColumnIndex())) {
	    	    		case 0:
	    	    			mainGroup = cell.getStringCellValue();
	    	    			break;
	    	    		case 1:
	    	    			subGroup = cell.getStringCellValue();
	    	    			break;
	    	    		case 2:
	    	    			name = cell.getStringCellValue();
	    	    			break;
	    	    		case 3:
	    	    			engName = cell.getStringCellValue();
	    	    			break;
	    	    		case 4:
	    	    			code = cell.getStringCellValue();
	    	    			break;
	    	    		case 5:
	    	    			spec = cell.getCellType().equals(CellType.STRING)? cell.getStringCellValue() :
	    	    				cell.getCellType().equals(CellType.NUMERIC)? String.valueOf(cell.getNumericCellValue()): "";
	    	    			break;
	    	    		case 6:
	    	    			unit = cell.getStringCellValue();
	    	    			break;
	    	    		case 7:
	    	    			toolbox = cell.getStringCellValue();
	    	    			break;
	    	    		case 8:
	    	    			count = (int) cell.getNumericCellValue();
	    	    			break;
	    	    		default:
	    	    			continue;
	    	    		}
	    	    		}catch (Exception e){
	    	    			results.add(cell.getAddress() + e.getMessage());
	    	    			e.printStackTrace();
	    	    			exceptionFlag = true;
	    	    		}
	    	    	}
	    	    }
	    	    if (row.getRowNum()!=0 && !exceptionFlag) {
	    	    	try {
						SubGroupDto subGroupDto = parseGroup(mainGroup,subGroup);
						
						if (subGroupDto == null) {
							logger.error("subGroupDto is NULL");
							throw new NullPointerException("subGroupDto is NULL");
						}
						
						ToolDto tool = ToolDto.builder()
										.code(code)
										.name(name)
										.engName(engName)
										.spec(spec)
										.unit(unit)
										//.price(datas[8].trim().isEmpty()?0:Integer.parseInt(datas[8].trim()))
										.subGroupDto(subGroupDto)									
										.build();
						ToolDto resultTool = toolService.update(subGroupDto.getId(), tool);
						logger.info(logCount + " : " + tool.toString());

						if (count!=null) {
							StockStatusDto stockDto = parseStock(resultTool.getId(),toolbox);
							stockStatusService.buyItems(stockDto.getToolDto().getId(),stockDto.getToolboxDto().getId(), count);
						}
						
						logCount++;
	    	    	
	    	    	}catch (Exception e) {
	    	    			results.add(row.getRowNum() + e.getMessage());
	    	    			exceptionFlag = true;
	    	    		}
//					ToolDto toolDto = toolService.getByCode(code);
//					if (toolDto==null) {
//						logger.info(logCount + " : NULL");
//						results.add(logCount + " : NULL");
//					}else {
//						logger.info(logCount + " : " + toolDto.getCode()+" , "+toolDto.getName()+" , "+toolDto.getSpec());
//						String key = toolDto.getCode();
//						if (resultMap.containsKey(key)) {
//				            // 현재 value에 1 추가
//				            int currentValue = resultMap.get(key);
//				            resultMap.put(key, currentValue + 1);
//				            results.add(logCount + " : "+resultMap.get(key));
//				        } else {
//				            // key가 존재하지 않으면 1로 초기화
//				            resultMap.put(key, 1);
//				        }
//					}
//					logCount++;
	    	    } else if (exceptionFlag) {
	    	    	continue;
	    	    }
	    	}
	    	logger.debug("total" + logCount+" items updated(added)");
	    	logger.debug("total "+ results.size() +" items skipped(error)");
	    	for (String s : results) {
	    		logger.error(s);
	    	}
	        return ResponseEntity.ok(results);
		} catch (Exception e) {
		    e.printStackTrace();
		    return ResponseEntity.ok(null);
		}
    }
    
    private SubGroupDto parseGroup(String mainGroupName, String subGroupName) {				
		MainGroupDto mainGroupDto = mainGroupService.get(mainGroupName);
		if (mainGroupDto == null) {
			mainGroupDto = MainGroupDto.builder()
					.name(mainGroupName)
					.build();
			mainGroupDto = mainGroupService.update(mainGroupDto);
		}
		
		SubGroupDto subGroupDto = subGroupService.get(mainGroupDto.getId(), subGroupName);
		if (subGroupDto == null) {
			subGroupDto = SubGroupDto.builder()
					.name(subGroupName)
					.mainGroupDto(mainGroupDto)
					.build();
			subGroupDto = subGroupService.update(mainGroupDto.getId(), subGroupDto);
		}
		
		return subGroupDto;
	}
	
	private StockStatusDto parseStock(long toolId, String toolboxName) {
		ToolboxDto toolboxDto = toolboxService.get(toolboxName);
		if (toolboxDto == null) {
			logger.debug(toolboxName + " toolbox is NULL");
			return null;
		}

		StockStatusDto stock= stockStatusService.get(toolId, toolboxDto.getId());
		if (stock == null) {
			return new StockStatusDto(stockStatusService.addNew(toolId, toolboxDto.getId(), 0));
		}else {
			return stock;
		}
	}
	
	@GetMapping(value="/stock_status/get/toolbox")
	public ResponseEntity<List<StockStatusSummaryByToolboxDto>> getStockStatusSummaryByToolbox(
			@RequestParam(name = "date") String date, @RequestParam(name = "toolId") Long toolId,
			@RequestParam(name = "subGroupId") Long subGroupId) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		List<StockStatusSummaryByToolboxDto> summaryDto = stockStatusService
				.findAllByToolAndSubGroupAndCurrentDay(localDate, toolId, subGroupId);
		return ResponseEntity.ok(summaryDto);
	}
}