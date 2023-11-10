package com.care4u.toolbox.sheet.rental.rental_request_tool;

import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalRequestToolDto {
	
	private long id;
	
	private ToolDto toolDto;
	
	private int count;
	
	private String Tags;
	
	@Builder
	public RentalRequestToolDto(long id, Tool tool, int count, String Tags) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.Tags = Tags;
	}
	
	public RentalRequestToolDto(RentalRequestTool rentalRequestTool) {
		this.id = rentalRequestTool.getId();
		this.toolDto = new ToolDto(rentalRequestTool.getTool());
		this.count = rentalRequestTool.getCount();
		this.Tags = rentalRequestTool.getTags();
	}
	
}
