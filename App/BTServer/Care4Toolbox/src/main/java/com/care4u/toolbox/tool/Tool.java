package com.care4u.toolbox.tool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.group.sub_group.SubGroup;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="tool")
public class Tool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private SubGroup subGroup;
	
	@NotNull
	@Column(unique = true)
	private String code;
	
	private String buyCode;
	
	private String engName;
	
	private String spec;
	
	private String unit;
	
	private int price;
	
	private int replacementCycle;
	
	public Tool(String code) {
		this.code = code;
	}
	
	@Builder
	public Tool(String name, SubGroup subGroup, String code, String buyCode, String engName, 
			String spec, String unit, int price, int replacementCycle) {
		this.name = name;
		this.subGroup = subGroup;
		this.code = code;
		this.buyCode = buyCode;
		this.engName = engName;
		this.spec = spec;
		this.unit = unit;
		this.price = price;
		this.replacementCycle = replacementCycle;
	}
	
	public void update(ToolDto toolDto, SubGroup subGroup) {
		this.name = toolDto.getName();
		this.subGroup = subGroup;
		this.buyCode = toolDto.getBuyCode();
		this.engName = toolDto.getEngName();
		this.spec = toolDto.getSpec();
		this.unit = toolDto.getUnit();
		this.price = toolDto.getPrice();
		this.replacementCycle = toolDto.getReplacementCycle();
	}
}