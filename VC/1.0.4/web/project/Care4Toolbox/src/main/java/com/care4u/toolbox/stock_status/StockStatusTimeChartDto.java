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
public class StockStatusTimeChartDto {
	
	private long id;
	
	private ToolboxDto toolboxDto;
	
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
	public StockStatusTimeChartDto(long id, Toolbox toolbox, LocalDate currentDay, int totalCount, int rentalCount, int buyCount,
			 int goodCount, int faultCount, int damageCount, int lossCount, int discardCount) {
		this.id = id;
		this.toolboxDto = new ToolboxDto(toolbox);
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
}
