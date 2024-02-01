package com.care4u.toolbox;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.hr.membership.Membership;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="toolbox")
public class Toolbox extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@Column(unique = true)
	private String name;
	
	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	private Membership manager;
	
	private boolean systemOperability;
	
	@Builder
	public Toolbox(String name, Membership manager, boolean systemOperability) {
		this.name = name;
		this.manager = manager;
		this.systemOperability = systemOperability;
	}
	
	public void update(String name, Membership manager, boolean systemOperability) {
		this.name = name;
		this.manager = manager;
		this.systemOperability = systemOperability;
	}
}