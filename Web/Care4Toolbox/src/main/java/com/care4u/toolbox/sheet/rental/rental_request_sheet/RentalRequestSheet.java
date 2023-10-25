package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.constant.SheetState;
import com.care4u.entity.BaseEntity;
import com.care4u.hr.membership.Membership;
import com.care4u.toolbox.Toolbox;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="rental_request_sheet")
public class RentalRequestSheet extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership worker;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership leader;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	private SheetState status;
	
	@NotNull
	private LocalDateTime eventTimestamp;
	
	@Builder
	public RentalRequestSheet(Membership worker, Membership leader, Toolbox toolbox, SheetState status, LocalDateTime eventTimestamp) {
		this.worker = worker;
		this.leader = leader;
		this.status = status;
		this.toolbox = toolbox;
		this.eventTimestamp = eventTimestamp;
	}
}