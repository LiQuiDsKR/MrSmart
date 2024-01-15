package com.care4u.toolbox.group.working_toolbox;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WorkingToolboxDto {
	
	private long id;
	
	private String name;
	
	private String remark;
	
	@Builder
	public WorkingToolboxDto(long id, String name, String remark) {
		this.id = id;
		this.name = name;
		this.remark = remark;
	}
	
	public WorkingToolboxDto(WorkingToolbox workingToolbox) {
		this.id = workingToolbox.getId();
		this.name = workingToolbox.getName();
		this.remark = workingToolbox.getRemark();
	}
	
}
