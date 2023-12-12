package com.care4u.toolbox.group.working_tool;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.group.working_toolbox.WorkingToolbox;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="working_tool")
public class WorkingTool extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkingToolbox workingToolbox;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	private int count;
	
	@Builder
	public WorkingTool(WorkingToolbox workingToolbox, Tool tool, int count) {
		this.workingToolbox = workingToolbox;
		this.tool = tool;
		this.count = count;
	}
	
	public void update(WorkingToolbox workingToolbox, Tool tool, int count) {
		this.workingToolbox = workingToolbox;
		this.tool = tool;
		this.count = count;
	}
}