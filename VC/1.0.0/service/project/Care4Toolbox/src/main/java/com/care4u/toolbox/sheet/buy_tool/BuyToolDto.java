package com.care4u.toolbox.sheet.buy_tool;

import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BuyToolDto {
	
	private long id;
	
	private ToolDto toolDto;
	
	private int count;
	
	private int price;
	
	@Builder
	public BuyToolDto(long id, Tool tool, int count, int price) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.price = price;
	}
	
	public BuyToolDto(BuyTool buyTool) {
		this.id = buyTool.getId();
		this.toolDto = new ToolDto(buyTool.getTool());
		this.count = buyTool.getCount();
		this.price = buyTool.getPrice();
	}
	
}
