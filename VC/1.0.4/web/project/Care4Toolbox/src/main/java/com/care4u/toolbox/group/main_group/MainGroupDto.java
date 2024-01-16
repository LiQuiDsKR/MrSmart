package com.care4u.toolbox.group.main_group;

import java.util.List;

import com.care4u.toolbox.group.sub_group.SubGroupDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class MainGroupDto {
	
	private long id;
	
	private String name;
	
	@Builder
	public MainGroupDto(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public MainGroupDto(MainGroup mainGroup) {
		this.id = mainGroup.getId();
		this.name = mainGroup.getName();
	}
		
}