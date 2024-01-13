package com.care4u.toolbox.tool;

import com.care4u.toolbox.group.sub_group.SubGroupDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolDto {
	
	private long id;
	
	private String name;
	
	private SubGroupDto subGroupDto;
	
	private String code;
	
	private String buyCode;
	
	private String engName;
	
	private String spec;
	
	private String unit;
	
	private int price;
	
	private int replacementCycle;
	
	@Builder
	public ToolDto(long id, String name, SubGroupDto subGroupDto, String code, String buyCode, String engName, 
			String spec, String unit, int price, int replacementCycle) {
		this.id = id;
		this.name = name;
		this.subGroupDto = subGroupDto;
		this.code = code;
		this.buyCode = buyCode;
		this.engName = engName;
		this.spec = spec;
		this.unit = unit;
		this.price = price;
		this.replacementCycle = replacementCycle;
	}
	
	public ToolDto(Tool tool) {
		this.id = tool.getId();
		this.name = tool.getName();
		this.subGroupDto = new SubGroupDto(tool.getSubGroup());
		this.code = tool.getCode();
		this.buyCode = tool.getBuyCode();
		this.engName = tool.getEngName();
		this.spec = tool.getSpec();
		this.unit = tool.getUnit();
		this.price = tool.getPrice();
		this.replacementCycle = tool.getReplacementCycle();
	}
	
}
