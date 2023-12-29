package com.care4u.toolbox.sheet.supply_tool;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupplyToolDto {
	
	private long id;
	
	private ToolDto toolDto;
	
	private int count;
	
	private LocalDate replacementDate;
	
	private MembershipDto worker;
	
	private MembershipDto leader;
	
	private MembershipDto approver;
	
	private ToolboxDto toolbox;
	
	private LocalDateTime eventTimestamp;
	
	@Builder
	public SupplyToolDto(long id, Tool tool, int count, LocalDate replacementDate) {
		this.id = id;
		this.toolDto = new ToolDto(tool);
		this.count = count;
		this.replacementDate = replacementDate;
	}
	
	public SupplyToolDto(SupplyTool supplyTool) {
		this.id = supplyTool.getId();
		this.toolDto = new ToolDto(supplyTool.getTool());
		this.count = supplyTool.getCount();
		this.replacementDate = supplyTool.getReplacementDate();
		this.worker = new MembershipDto(supplyTool.getSupplySheet().getWorker());
		this.leader = new MembershipDto(supplyTool.getSupplySheet().getLeader());
		this.approver = new MembershipDto(supplyTool.getSupplySheet().getApprover());
		this.toolbox = new ToolboxDto(supplyTool.getSupplySheet().getToolbox());
		this.eventTimestamp = supplyTool.getSupplySheet().getEventTimestamp();
	}
	
}
