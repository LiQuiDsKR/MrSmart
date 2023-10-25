package com.care4u.toolbox.stock_status;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="stock_status")
public class StockStatus extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	@NotNull
	LocalDate currentDay;
	
	private int totalCount;
	
	private int rentalCount;
	
	private int buyCount;
	
	private int goodCount;
	
	private int faultCount;
	
	private int damageCount;
	
	private int lossCount;
	
	private int discardCount;
	
	@Builder
	public StockStatus(Toolbox toolbox, Tool tool, LocalDate currentDay, int totalCount, int rentalCount, int buyCount,
			int goodCount, int faultCount, int damageCount, int lossCount, int discardCount) {
		this.toolbox = toolbox;
		this.tool = tool;
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