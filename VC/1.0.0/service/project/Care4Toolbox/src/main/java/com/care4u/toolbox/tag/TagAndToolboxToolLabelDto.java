package com.care4u.toolbox.tag;

import java.util.List;

import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.tool.ToolDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabel;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class TagAndToolboxToolLabelDto {

	private List<TagDto> tagDtoList;
	
	private ToolboxToolLabelDto toolboxToolLabelDto;
	
}