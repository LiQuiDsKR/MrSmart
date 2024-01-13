package com.care4u.hr.part;

import com.care4u.hr.sub_part.SubPartDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PartDto {
	
	private long id;
	
	private String name;
	
	private SubPartDto subPartDto;
	
	@Builder
	public PartDto(long id, String name, SubPartDto subPartDto) {
		this.id = id;
		this.name = name;
		this.subPartDto = subPartDto;
	}
	
	public PartDto(Part part) {
		this.id = part.getId();
		this.name = part.getName();
		this.subPartDto = new SubPartDto(part.getSubPart());
	}
	
}
