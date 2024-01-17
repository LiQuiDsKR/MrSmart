package com.care4u.toolbox.sheet.buy_tool;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.sheet.buy_sheet.BuySheet;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="buy_tool")
public class BuyTool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private BuySheet buySheet;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	private int count;
	
	private int price;
	
	@Builder
	public BuyTool(BuySheet buySheet, Tool tool, int count, int price) {
		this.buySheet = buySheet;
		this.tool = tool;
		this.count = count;
		this.price = price;
	}
}