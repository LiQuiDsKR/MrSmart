package com.care4u.toolbox.sheet.supply_sheet;

import java.time.LocalDateTime;
import java.util.List;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.supply_tool.SupplyTool;
import com.care4u.toolbox.sheet.supply_tool.SupplyToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupplySheetDto {
	
	private long id;
	
	private MembershipDto workerDto;
	
	private MembershipDto leaderDto;
	
	private MembershipDto approverDto;
	
	private ToolboxDto toolboxDto;
	
	private LocalDateTime eventTimestamp;
	
	private List<SupplyToolDto> toolList;
	
	@Builder
	public SupplySheetDto(long id, Membership worker, Membership leader, Membership approver, Toolbox toolboxDto, LocalDateTime eventTimestamp,List<SupplyToolDto> toolList) {
		this.id = id;
		this.workerDto = new MembershipDto(worker);
		this.leaderDto = new MembershipDto(leader);
		this.approverDto = new MembershipDto(approver);
		this.toolboxDto = new ToolboxDto(toolboxDto);
		this.eventTimestamp = eventTimestamp;
		this.toolList = toolList;
	}
	
	public SupplySheetDto(SupplySheet supplySheet,List<SupplyToolDto> toolList) {
		this.id = supplySheet.getId();
		this.workerDto = new MembershipDto(supplySheet.getWorker());
		this.leaderDto = new MembershipDto(supplySheet.getLeader());
		this.approverDto = new MembershipDto(supplySheet.getApprover());
		this.toolboxDto = new ToolboxDto(supplySheet.getToolbox());
		this.eventTimestamp = supplySheet.getEventTimestamp();
		this.toolList = toolList;
	}
	
}
