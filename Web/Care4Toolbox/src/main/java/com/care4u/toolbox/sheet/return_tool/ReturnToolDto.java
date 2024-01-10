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
	
	private RentalToolDto rentalToolDto;
	
	private String Tags;
	
	private int count;
	
	private int goodCount;
	
	private int faultCount;
	
	private int damageCount;
	
	private int lossCount;
	
	private int discardCount;
	
	private String comment;
	
	@Builder
	public ReturnToolDto(long id, RentalTool rentalTool, String Tags,
			int count, int goodCount, int faultCount, int damageCount, int lossCount, int discardCount, String rentalTags, String comment) {
		this.id = id;
		this.rentalToolDto = new RentalToolDto(rentalTool,rentalTags);
		this.Tags = Tags;
		this.count = count;
		this.goodCount = goodCount;
		this.faultCount = faultCount;
		this.damageCount = damageCount;
		this.lossCount = lossCount;
		this.discardCount = discardCount;
		this.comment=comment;
	}
	
	public ReturnToolDto(ReturnTool returnTool, String rentalTags) {
		this.id = returnTool.getId();
		this.rentalToolDto = new RentalToolDto(returnTool.getRentalTool(),rentalTags);
		this.Tags = returnTool.getTags();
		this.count = returnTool.getCount();
		this.goodCount = returnTool.getGoodCount();
		this.faultCount = returnTool.getFaultCount();
		this.damageCount = returnTool.getDamageCount();
		this.lossCount = returnTool.getLossCount();
		this.discardCount = returnTool.getDiscardCount();
		this.comment=returnTool.getComment();
	}
	
}
