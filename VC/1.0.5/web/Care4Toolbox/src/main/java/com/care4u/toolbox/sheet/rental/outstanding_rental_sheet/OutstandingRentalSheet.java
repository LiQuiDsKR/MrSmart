package com.care4u.toolbox.sheet.rental.outstanding_rental_sheet;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.constant.OutstandingState;
import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="outstanding_rental_tool")
public class OutstandingRentalSheet extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	private RentalSheet rentalSheet;
	
	private int totalCount;
	
	private int totalOutstandingCount;
	
	@Enumerated(EnumType.STRING)
	private OutstandingState outstandingStatus;
	
	
	
	@Builder
	public OutstandingRentalSheet(RentalSheet rentalSheet, int totalCount, int totalOutstandingCount, OutstandingState outstandingStatus) {
		this.rentalSheet = rentalSheet;
		this.totalCount = totalCount;
		this.totalOutstandingCount = totalOutstandingCount;
		this.outstandingStatus = outstandingStatus;
	}
	
	public void updateOutstandingCount(int totalOutstandingCount) {
		this.totalOutstandingCount = totalOutstandingCount;
	}
	
	public void updateOutstandingState(OutstandingState outstandingStatus) {
		this.outstandingStatus = outstandingStatus;
	}
}