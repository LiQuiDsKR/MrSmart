package com.care4u.toolbox.tool;

import javax.validation.Valid;

import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolForRentalDto {
	
	@Valid
	private ToolDto toolDto;
	@Valid
	private StockStatusDto stockDto;
	@Valid
	private ToolboxToolLabelDto labelDto;
	
	
	@Builder
	public ToolForRentalDto(ToolDto toolDto, StockStatusDto stockDto, ToolboxToolLabelDto labelDto) {
		this.toolDto = toolDto;
		this.stockDto = stockDto;
		this.labelDto = labelDto;
	}
}
