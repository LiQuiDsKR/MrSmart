package com.care4u.toolbox.sheet.buy_sheet;

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

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="buy_sheet")
public class BuySheet extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Membership approver;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	private LocalDateTime eventTimestamp;
	
	@Builder
	public BuySheet(Membership approver, Toolbox toolbox, LocalDateTime eventTimestamp) {
		this.approver = approver;
		this.toolbox = toolbox;
		this.eventTimestamp = eventTimestamp;
	}
}