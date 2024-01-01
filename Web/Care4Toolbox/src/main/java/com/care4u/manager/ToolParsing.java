package com.care4u.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.ToolboxService;
import com.care4u.toolbox.group.main_group.MainGroupDto;
import com.care4u.toolbox.group.main_group.MainGroupService;
import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.group.sub_group.SubGroupService;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.stock_status.StockStatusService;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.tool.ToolService;

import lombok.Getter;

@Getter
public class ToolParsing {
	private final Logger logger = Logger.getLogger(ToolParsing.class);
	
	private MainGroupService mainGroupService;
	private SubGroupService subGroupService;
	private ToolService toolService;
	private StockStatusService stockStatusService;
	private ToolboxService toolboxService;
	
	public ToolParsing(MainGroupService mainGroupService, SubGroupService subGroupService, ToolService toolService) {
		this.mainGroupService = mainGroupService;
		this.subGroupService = subGroupService;
		this.toolService = toolService;
	}
	
	public void readCsvFile(String csvFilePath) {
		
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
			String line;
			
			// 라인 단위로 읽어오기
			int count = 1;
			while ((line = reader.readLine()) != null) {
				// ','로 구분된 데이터 파싱
				String[] datas = line.split(",");
				
				if (datas.length > 7) {
					SubGroupDto subGroupDto = parseGroup(datas[1].trim(), datas[2].trim());
					
					if (subGroupDto == null) {
						logger.error("subGroupDto is NULL");
						return;
					}
					
					ToolDto tool = ToolDto.builder()
									.code(datas[3].trim())
									.name(datas[4].trim())
									.engName(datas[5].trim())
									.spec(datas[6].trim())
									.unit(datas[7].trim())
									//.price(datas[8].trim().isEmpty()?0:Integer.parseInt(datas[8].trim()))
									.subGroupDto(subGroupDto)									
									.build();
					toolService.update(subGroupDto.getId(), tool);
					logger.info(count + " : " + tool.toString());

					if (datas.length>9) {
						StockStatusDto stockDto = parseStock(tool.getId(),datas[9].trim());
						stockStatusService.buyItems(stockDto.getToolDto().getId(),stockDto.getToolboxDto().getId(), Integer.parseInt(datas[8].trim()));
					}
					
					count++;
				}
			}
		} catch (IOException e) {
			logger.error(e.toString());
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
			toolboxDto = ToolboxDto.builder()
					.name(toolboxName)
					.build();
			toolboxDto = toolboxService.update(toolboxDto);
		}

		StockStatusDto stock= stockStatusService.get(toolId, toolboxDto.getId());
		if (stock == null) {
			return new StockStatusDto(stockStatusService.addNew(toolId, toolboxDto.getId(), 0));
		}else {
			return stock;
		}
	}
	
	public void checkCsvFile(String csvFilePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
			String line;
			
			// 라인 단위로 읽어오기
			int count = 1;
			List<String> results = new ArrayList<String> ();
			Map<String, Integer> resultMap = new HashMap<>();
			while ((line = reader.readLine()) != null) {
				// ','로 구분된 데이터 파싱
				String[] datas = line.split(",");
				ToolDto toolDto = toolService.getByCode(datas[3].trim());
				if (toolDto==null) {
					logger.info(count + " : NULL");
					results.add(count + " : NULL");
				}else {
					logger.info(count + " : " + toolDto.getCode()+" , "+toolDto.getName()+" , "+toolDto.getSpec());
					String key = toolDto.getCode();
					if (resultMap.containsKey(key)) {
			            // 현재 value에 1 추가
			            int currentValue = resultMap.get(key);
			            resultMap.put(key, currentValue + 1);
			            results.add(count + " : "+resultMap.get(key));
			        } else {
			            // key가 존재하지 않으면 1로 초기화
			            resultMap.put(key, 1);
			        }
				}
				count++;
			}
			for (String s : results) {				
				logger.debug(s);
			}
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

}
