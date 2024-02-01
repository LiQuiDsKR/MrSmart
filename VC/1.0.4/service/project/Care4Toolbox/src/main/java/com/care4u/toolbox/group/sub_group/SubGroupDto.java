package com.care4u.toolbox.group.sub_group;

import com.care4u.toolbox.group.main_group.MainGroupDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SubGroupDto {
	
	private long id;
	
	private String name;
	
	private MainGroupDto mainGroupDto;
	
	@Builder
	public SubGroupDto(long id, String name, MainGroupDto mainGroupDto) {
		this.id = id;
		this.name = name;
		this.mainGroupDto = mainGroupDto;
	}
	
	public SubGroupDto(SubGroup subGroup) {
		this.id = subGroup.getId();
		this.name = subGroup.getName();
		this.mainGroupDto = new MainGroupDto(subGroup.getMainGroup());
	}
	
}
