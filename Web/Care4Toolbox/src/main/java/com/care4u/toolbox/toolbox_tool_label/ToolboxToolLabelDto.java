package com.care4u.toolbox.toolbox_tool_label;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolboxToolLabelDto {
	
	private long id;
	
	@NotNull
	private ToolboxDto toolboxDto;
	
	private String location;
	
	@NotNull
	private ToolDto toolDto;
	
	@NotEmpty
	private String qrcode;
	
	@Builder
	public ToolboxToolLabelDto(long id, ToolboxDto toolboxDto, String location, ToolDto toolDto) {
		this.id = id;
		this.toolboxDto = toolboxDto;
		this.location = location;
		this.toolDto = toolDto;
	}
	
	public ToolboxToolLabelDto(ToolboxToolLabel toolboxTool) {
		this.id = toolboxTool.getId();
		this.toolboxDto = new ToolboxDto(toolboxTool.getToolbox());
		this.location = toolboxTool.getLocation();
		this.toolDto = new ToolDto(toolboxTool.getTool());
	}
	
}
