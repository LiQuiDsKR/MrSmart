package com.care4u.toolbox.sheet.return_tool;

import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalToolDto;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheetDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReturnToolDto {
	
	private long id;
	
	private ReturnSheetDto returnSheetDto;
	
	private RentalToolDto rentalToolDto;
	
	private String Tags;
	
	private int count;
	
	private int goodCount;
	
	private int faultCount;
	
	private int damageCount;
	
	private int lossCount;
	
	private int discardCount;
	
	@Builder
	public ReturnToolDto(long id, ReturnSheet returnSheet, RentalTool rentalTool, String Tags,
			int count, int goodCount, int faultCount, int damageCount, int lossCount, int discardCount) {
		this.id = id;
		this.returnSheetDto = new ReturnSheetDto(returnSheet);
		this.rentalToolDto = new RentalToolDto(rentalTool);
		this.Tags = Tags;
		this.count = count;
		this.goodCount = goodCount;
		this.faultCount = faultCount;
		this.damageCount = damageCount;
		this.lossCount = lossCount;
		this.discardCount = discardCount;
	}
	
	public ReturnToolDto(ReturnTool rentalTool) {
		this.id = rentalTool.getId();
		this.returnSheetDto = new ReturnSheetDto(rentalTool.getReturnSheet());
		this.rentalToolDto = new RentalToolDto(rentalTool.getRentalTool());
		this.Tags = rentalTool.getTags();
		this.count = rentalTool.getCount();
		this.goodCount = rentalTool.getGoodCount();
		this.faultCount = rentalTool.getFaultCount();
		this.damageCount = rentalTool.getDamageCount();
		this.lossCount = rentalTool.getLossCount();
		this.discardCount = rentalTool.getDiscardCount();
	}
	
}
