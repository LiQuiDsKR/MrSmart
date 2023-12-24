package com.care4u.hr.part;

import com.care4u.hr.main_part.MainPart;
import com.care4u.hr.main_part.MainPartDto;
import com.care4u.hr.sub_part.SubPart;
import com.care4u.hr.sub_part.SubPartDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PartWrapperDto {

	private enum PartType{
		PART,SUB_PART,MAIN_PART
	}
	
	private PartType type;
	
	private PartDto partDto;
	
	private SubPartDto subPartDto;
	
	private MainPartDto mainPartDto;
	
	public PartWrapperDto(Part part) {
		this.type=PartType.PART;
		this.partDto=new PartDto(part);
		this.subPartDto=partDto.getSubPartDto();
		this.mainPartDto=partDto.getSubPartDto().getMainPartDto();
	}
	public PartWrapperDto(SubPart subPart) {
		this.type=PartType.SUB_PART;
		this.subPartDto=new SubPartDto(subPart);
		this.mainPartDto=subPartDto.getMainPartDto();
	}
	public PartWrapperDto(MainPart mainPart) {
		this.type=PartType.MAIN_PART;
		this.mainPartDto=new MainPartDto(mainPart);
	}

	public PartWrapperDto(PartDto partDto) {
		this.type=PartType.PART;
		this.partDto=partDto;
		this.subPartDto=partDto.getSubPartDto();
		this.mainPartDto=partDto.getSubPartDto().getMainPartDto();
	}
	public PartWrapperDto(SubPartDto subPartDto) {
		this.type=PartType.SUB_PART;
		this.subPartDto=subPartDto;
		this.mainPartDto=subPartDto.getMainPartDto();
	}
	public PartWrapperDto(MainPartDto mainPartDto) {
		this.type=PartType.MAIN_PART;
		this.mainPartDto=mainPartDto;
	}
}
