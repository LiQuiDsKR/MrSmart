package com.care4u.toolbox.stock_status;

import java.time.LocalDate;

import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StockStatusDto {
	
	private long id;
	
	private ToolboxDto toolboxDto;
	
	private ToolDto toolDto;
	
	private LocalDate currentDay;
		
	private int totalCount;
	
	private int rentalCount;
	
	private int buyCount;
	
	private int goodCount;
	
	private int faultCount;
	
	private int damageCount;
	
	private int lossCount;
	
	private int discardCount;
	
	@Builder
	public StockStatusDto(long id, Toolbox toolbox, Tool tool, LocalDate currentDay, int totalCount, int rentalCount, int buyCount,
			 int goodCount, int faultCount, int damageCount, int lossCount, int discardCount) {
		this.id = id;
		this.toolboxDto = new ToolboxDto(toolbox);
		this.toolDto = new ToolDto(tool);
		this.currentDay = currentDay;
		this.totalCount = totalCount;
		this.rentalCount = rentalCount;
		this.buyCount = buyCount;
		this.goodCount = goodCount;
		this.faultCount = faultCount;
		this.damageCount = damageCount;
		this.lossCount = lossCount;
		this.discardCount = discardCount;
	}
	
	public StockStatusDto(StockStatus stockStatus) {
		this.id = stockStatus.getId();
		this.toolboxDto = new ToolboxDto(stockStatus.getToolbox());
		this.toolDto = new ToolDto(stockStatus.getTool());
		this.currentDay = stockStatus.getCurrentDay();
		this.totalCount = stockStatus.getTotalCount();
		this.rentalCount = stockStatus.getRentalCount();
		this.buyCount = stockStatus.getBuyCount();
		this.goodCount = stockStatus.getGoodCount();
		this.faultCount = stockStatus.getFaultCount();
		this.damageCount = stockStatus.getDamageCount();
		this.lossCount = stockStatus.getLossCount();
		this.discardCount = stockStatus.getDiscardCount();
	}
	
}
