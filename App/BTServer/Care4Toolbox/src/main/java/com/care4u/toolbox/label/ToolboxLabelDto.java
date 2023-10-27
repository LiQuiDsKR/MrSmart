package com.care4u.toolbox.label;

import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolboxLabelDto {
	
	private long id;
	
	private ToolboxDto toolboxDto;
	
	private String location;
	
	private ToolDto toolDto;
	
	@Builder
	public ToolboxLabelDto(long id, ToolboxDto toolboxDto, String location, ToolDto toolDto) {
		this.id = id;
		this.toolboxDto = toolboxDto;
		this.location = location;
		this.toolDto = toolDto;
	}
	
	public ToolboxLabelDto(ToolboxLabel toolboxTool) {
		this.id = toolboxTool.getId();
		this.toolboxDto = new ToolboxDto(toolboxTool.getToolbox());
		this.location = toolboxTool.getLocation();
		this.toolDto = new ToolDto(toolboxTool.getTool());
	}
	
}
