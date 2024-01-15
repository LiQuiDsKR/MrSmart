package com.care4u.toolbox;

import com.care4u.hr.membership.MembershipDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

public class ToolboxCompressDto {
	
	private long id;
	
	private String name;

	@Builder
	public ToolboxCompressDto(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public ToolboxCompressDto(Toolbox toolbox) {
		this.id = toolbox.getId();
		this.name = toolbox.getName();
	}
		
}