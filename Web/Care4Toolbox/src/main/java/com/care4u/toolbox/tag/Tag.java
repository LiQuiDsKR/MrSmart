package com.care4u.toolbox.tag;

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
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.sheet.rental.rental_tool.RentalTool;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="tag")
public class Tag extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@Column(unique = true)
	private String macaddress;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private RentalTool rentalTool;
	
	@Builder
	public Tag(String macaddress, Toolbox toolbox, Tool tool) {
		this.macaddress = macaddress;
		this.toolbox = toolbox;
		this.tool = tool;
	}
	
	public void update(Toolbox toolbox) {
		this.toolbox = toolbox;
	}
	
	public void update(RentalTool rentalTool) {
		this.rentalTool = rentalTool;
	}
}