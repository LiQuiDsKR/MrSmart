package com.care4u.toolbox.tag;

import java.util.List;

import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TagDto {
	
	private long id;
	
	private String macaddress;
	
	private ToolboxDto toolboxDto;
	
	private ToolDto toolDto;
	
	private RentalToolDto rentalToolDto;
	
	private String tagGroup;
	
	@Builder
	public TagDto(long id, String macaddress, ToolboxDto toolboxDto, ToolDto toolDto, RentalToolDto rentalToolDto, String TagGroup) {
		this.id = id;
		this.macaddress = macaddress;
		this.toolboxDto = toolboxDto;
		this.toolDto = toolDto;
		this.rentalToolDto = rentalToolDto;
		this.tagGroup = tagGroup;
	}
	
	public TagDto(Tag tag, String rentalToolTags) {
		this.id = tag.getId();
		this.macaddress = tag.getMacaddress();
		this.toolboxDto = new ToolboxDto(tag.getToolbox());
		this.toolDto = new ToolDto(tag.getTool());
		this.rentalToolDto = tag.getRentalTool()==null?null:new RentalToolDto(tag.getRentalTool(),rentalToolTags);
		this.tagGroup = tag.getTagGroup();
	}
	
	public TagDto(Tag tag) {
		this.id = tag.getId();
		this.macaddress = tag.getMacaddress();
		this.toolboxDto = new ToolboxDto(tag.getToolbox());
		this.toolDto = new ToolDto(tag.getTool());
		this.rentalToolDto = null;
		this.tagGroup = tag.getTagGroup();
	}
	
}
