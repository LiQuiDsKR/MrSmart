package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestTool;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalRequestSheetDto {
	
	private long id;
	
	@Valid
	private MembershipDto workerDto;
	@Valid
	private MembershipDto leaderDto;
	@Valid
	private ToolboxDto toolboxDto;
	
	private SheetState status;
	
	private LocalDateTime eventTimestamp;
	
	private List<RentalRequestToolDto> toolList;
	
	@Builder
	public RentalRequestSheetDto(long id, Membership worker, Membership leader, Toolbox toolbox, SheetState status, LocalDateTime eventTimestamp, List<RentalRequestToolDto> toolList) {
		this.id = id;
		this.workerDto = new MembershipDto(worker);
		this.leaderDto = new MembershipDto(leader);
		this.toolboxDto = new ToolboxDto(toolbox);
		this.status = status;
		this.eventTimestamp = eventTimestamp;
		this.toolList = toolList;
	}
	
	public RentalRequestSheetDto(RentalRequestSheet sheet, List<RentalRequestToolDto> toolList) {
		this.id = sheet.getId();
		this.workerDto = new MembershipDto(sheet.getWorker());
		this.leaderDto = new MembershipDto(sheet.getLeader());
		this.toolboxDto = new ToolboxDto(sheet.getToolbox());
		this.status = sheet.getStatus();
		this.eventTimestamp = sheet.getEventTimestamp();
		this.toolList = toolList;
	}
	
}
