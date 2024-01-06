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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.sheet.return_tool.ReturnToolService;
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
	
	private final Logger logger = LoggerFactory.getLogger(StockStatus.class);
	
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
	
	private int supplyCount;
	
	private int returnCount;
	
	@Builder
	public StockStatus(Toolbox toolbox, Tool tool, LocalDate currentDay, int totalCount, int rentalCount, int buyCount,
			int goodCount, int faultCount, int damageCount, int lossCount, int discardCount, int supplyCount, int returnCount) {
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
		this.supplyCount = supplyCount;
		this.returnCount = returnCount;
		logger.debug(getCreatedBy());
		logger.debug("stock created : "+this.toString());
	}
	
	public void update(Toolbox toolbox, Tool tool, LocalDate currentDay, int totalCount, int rentalCount, int buyCount,
			int goodCount, int faultCount, int damageCount, int lossCount, int discardCount, int supplyCount, int returnCount) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from : "+this.toString());
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
		this.supplyCount = supplyCount;
		this.returnCount = returnCount;
		logger.debug("stock updated to : "+this.toString());
	}
	
	public void requestUpdate(int count) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from (request) : "+this.toString());
		this.goodCount-=count;
		logger.debug("stock updated to (request) : "+this.toString());
	}
	public void requestCancelUpdate(int count) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from (request Cancel) : "+this.toString());
		this.goodCount+=count;
		logger.debug("stock updated to (request Cancel) : "+this.toString());
	}
	public void rentUpdate(int count) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from (rent) : "+this.toString());
		this.goodCount-=count;
		this.rentalCount+=count;
		logger.debug("stock updated to (rent) : "+this.toString());
	}
	public void returnUpdate(int goodCount, int faultCount, int damageCount, int discardCount, int lossCount) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from (return) : "+this.toString());
		this.goodCount+=goodCount;
		this.faultCount+=faultCount;
		this.damageCount+=damageCount;
		this.discardCount+=discardCount;
		this.lossCount+=lossCount;
		this.rentalCount-=(goodCount+faultCount+damageCount+discardCount+lossCount);
		this.returnCount+=(goodCount+faultCount+damageCount+discardCount+lossCount);
		this.totalCount-=(discardCount+lossCount);
		logger.debug("stock updated to (return) : "+this.toString());
	}
	public void buyUpdate(int count) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated from (buy) : "+this.toString());
		this.buyCount+=count;
		this.goodCount+=count;
		this.totalCount+=count;
		logger.debug("stock updated to (buy) : "+this.toString());
	}
	public void supplyUpdate(int count) {
		logger.debug(getModifiedBy());
		logger.debug("stock updated to (supply) : "+this.toString());
		this.goodCount-=count;
		this.supplyCount+=count;
		this.totalCount-=count;
		logger.debug("stock updated to (supply) : "+this.toString());
	}
}