package com.care4u.toolbox.sheet.return_tool;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.sheet.return_sheet.ReturnSheet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="return_tool")
public class ReturnTool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private ReturnSheet returnSheet;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private RentalTool rentalTool;
	
	private int count;
	
	private int goodCount;
	
	private int faultCount;
	
	private int damageCount;
	
	private int lossCount;
	
	private int discardCount;
	
	private String Tags;
	
	@Builder
	public ReturnTool(ReturnSheet returnSheet, RentalTool rentalTool, int count, String Tags,
			int goodCount, int faultCount, int damageCount, int lossCount, int discardCount) {
		this.returnSheet = returnSheet;
		this.rentalTool = rentalTool;
		this.count = count;
		this.goodCount = goodCount;
		this.faultCount = faultCount;
		this.damageCount = damageCount;
		this.lossCount = lossCount;
		this.discardCount = discardCount;
		this.Tags = Tags;
	}
	public void updateCount(int goodCount, int faultCount, int damageCount, int lossCount, int discardCount, String Tags) {
		this.count += goodCount+faultCount+damageCount+lossCount+discardCount;
		this.goodCount = goodCount;
		this.faultCount = faultCount;
		this.damageCount = damageCount;
		this.lossCount = lossCount;
		this.discardCount = discardCount;
		this.Tags+=Tags;
	}
}