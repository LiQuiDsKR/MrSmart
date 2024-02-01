package com.care4u.toolbox.sheet.supply_tool;

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
import com.care4u.toolbox.sheet.supply_sheet.SupplySheet;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="supply_tool")
public class SupplyTool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private SupplySheet supplySheet;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	private int count;
	
	private LocalDate replacementDate;
	
	@Builder
	public SupplyTool(SupplySheet supplySheet, Tool tool, int count, LocalDate replacementDate) {
		this.supplySheet = supplySheet;
		this.tool = tool;
		this.count = count;
		this.replacementDate = replacementDate;
	}
}