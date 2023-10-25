package com.care4u.toolbox.sheet.rental.rental_tool;

import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalToolDto {
	
	private long id;
	
	private ToolDto toolDto;
	
	private int count;
	
	private int outstandingCount;
	
	private String Tags;
	
	@Builder
	public RentalToolDto(long id, Tool tool, int count, int outstandingCount, String Tags) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.outstandingCount = outstandingCount;
		this.Tags = Tags;
	}
	
	public RentalToolDto(RentalTool rentalTool) {
		this.id = rentalTool.getId();
		this.toolDto = new ToolDto(rentalTool.getTool());
		this.count = rentalTool.getCount();
		this.outstandingCount = rentalTool.getOutstandingCount();
		this.Tags = rentalTool.getTags();
	}
	
}
