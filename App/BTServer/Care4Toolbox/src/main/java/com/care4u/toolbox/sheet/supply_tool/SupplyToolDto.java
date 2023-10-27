package com.care4u.toolbox.sheet.supply_tool;

import java.time.LocalDate;

import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupplyToolDto {
	
	private long id;
	
	private ToolDto toolDto;
	
	private int count;
	
	private LocalDate replacementDate;
	
	@Builder
	public SupplyToolDto(long id, Tool tool, int count, LocalDate replacementDate) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.replacementDate = replacementDate;
	}
	
	public SupplyToolDto(SupplyTool supplyTool) {
		this.id = supplyTool.getId();
		this.toolDto = new ToolDto(supplyTool.getTool());
		this.count = supplyTool.getCount();
		this.replacementDate = supplyTool.getReplacementDate();
	}
	
}
