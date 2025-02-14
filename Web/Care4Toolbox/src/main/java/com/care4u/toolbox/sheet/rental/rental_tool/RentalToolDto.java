package com.care4u.toolbox.sheet.rental.rental_tool;

import java.util.List;

import com.care4u.toolbox.tag.Tag;
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
	
	private List<Tag> tagList;
	
	@Builder
	public RentalToolDto(long id, Tool tool, int count, int outstandingCount, List<Tag> tagList) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.outstandingCount = outstandingCount;
		this.tagList = tagList;
	}
	
	public RentalToolDto(RentalTool rentalTool, List<Tag> tagList) {
		this.id = rentalTool.getId();
		this.toolDto = new ToolDto(rentalTool.getTool());
		this.count = rentalTool.getCount();
		this.outstandingCount = rentalTool.getOutstandingCount();
		this.tagList = tagList;
	}
	
}
