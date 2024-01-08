package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="return_sheet")
public class ReturnSheet extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private RentalSheet rentalSheet;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership worker;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership leader;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership approver;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	private LocalDateTime eventTimestamp;
	
	@Builder
	public ReturnSheet(RentalSheet rentalSheet, Membership worker, Membership approver, Toolbox toolbox, LocalDateTime eventTimestamp) {
		this.rentalSheet = rentalSheet;
		this.worker = worker;
		this.leader = rentalSheet.getLeader();
		this.approver = approver;
		this.toolbox = toolbox;
		this.eventTimestamp = eventTimestamp;
	}

	public void updateEventTimestamp(LocalDateTime eventTimestamp) {
		this.eventTimestamp = eventTimestamp;		
	}
}