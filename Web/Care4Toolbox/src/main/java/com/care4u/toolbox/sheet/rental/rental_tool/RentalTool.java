package com.care4u.toolbox.sheet.rental.rental_tool;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.sheet.rental.rental_request_sheet.RentalRequestSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="rental_tool")
public class RentalTool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private RentalSheet rentalSheet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private RentalRequestSheet rentalRequestSheet;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	private int count;
	
	private int outstandingCount;
	
	@Builder
	public RentalTool(RentalSheet rentalSheet, Tool tool, int count, int outstandingCount, String Tags, RentalRequestSheet rentalRequestSheet) {
		this.rentalSheet = rentalSheet;
		this.tool = tool;
		this.count = count;
		this.outstandingCount = outstandingCount;
		this.rentalRequestSheet = rentalRequestSheet;
	}
}