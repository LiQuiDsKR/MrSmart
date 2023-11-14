package com.care4u.toolbox.tool;

import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolForRentalDto {
	
	private ToolDto toolDto;
	
	private StockStatusDto stockDto;
	
	private ToolboxToolLabelDto labelDto;
	
	
	@Builder
	public ToolForRentalDto(ToolDto toolDto, StockStatusDto stockDto, ToolboxToolLabelDto labelDto) {
		this.toolDto = toolDto;
		this.stockDto = stockDto;
		this.labelDto = labelDto;
	}
}
