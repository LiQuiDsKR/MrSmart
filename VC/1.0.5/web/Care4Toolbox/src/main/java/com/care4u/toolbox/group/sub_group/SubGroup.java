package com.care4u.toolbox.group.sub_group;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.group.main_group.MainGroup;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="sub_group")
public class SubGroup extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private MainGroup mainGroup;
	
	@Builder
	public SubGroup(String name, MainGroup mainGroup) {
		this.name = name;
		this.mainGroup = mainGroup;
	}
	
	public void update(SubGroupDto subGroupDto, MainGroup mainGroup) {
		this.name = subGroupDto.getName();
		this.mainGroup = mainGroup;
	}
}