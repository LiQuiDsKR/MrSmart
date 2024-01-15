package com.care4u.toolbox;

import com.care4u.hr.membership.MembershipDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

public class ToolboxDto {
	
	private long id;
	
	private String name;
	
	private MembershipDto managerDto;
	
	private boolean systemOperability;
	
	@Builder
	public ToolboxDto(long id, String name, MembershipDto managerDto, boolean systemOperability) {
		this.id = id;
		this.name = name;
		this.managerDto = managerDto;
		this.systemOperability = systemOperability;
	}
	
	public ToolboxDto(Toolbox toolbox) {
		this.id = toolbox.getId();
		this.name = toolbox.getName();
		this.managerDto = new MembershipDto(toolbox.getManager());
		this.systemOperability = toolbox.isSystemOperability();
	}
		
}