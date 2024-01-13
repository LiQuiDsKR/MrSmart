package com.care4u.toolbox.group.working_toolbox;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="working_toolbox")
public class WorkingToolbox extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	private String name;
	
	private String remark;
	
	@Builder
	public WorkingToolbox(String name, String remark) {
		this.name = name;
		this.remark = remark;
	}
	
	public void update(WorkingToolboxDto workingToolboxDto) {
		this.name = workingToolboxDto.getName();
		this.remark = workingToolboxDto.getRemark();
	}
}