package com.care4u.toolbox.group.working_tool;

import com.care4u.toolbox.group.working_toolbox.WorkingToolboxDto;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WorkingToolDto {
	
	private long id;
	
	private WorkingToolboxDto workingToolboxDto;
	
	private ToolDto toolDto;
	
	private int count;
	
	@Builder
	public WorkingToolDto(long id, WorkingToolboxDto workingToolboxDto, ToolDto toolDto, int count) {
		this.id = id;
		this.workingToolboxDto = workingToolboxDto;
		this.toolDto = toolDto;
		this.count = count;
	}
	
	public WorkingToolDto(WorkingTool workingTool) {
		this.id = workingTool.getId();
		this.workingToolboxDto = new WorkingToolboxDto(workingTool.getWorkingToolbox());
		this.toolDto = new ToolDto(workingTool.getTool());
		this.count = workingTool.getCount();
	}
	
}
