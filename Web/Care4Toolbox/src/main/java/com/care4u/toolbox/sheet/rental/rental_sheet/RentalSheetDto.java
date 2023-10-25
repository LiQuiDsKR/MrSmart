package com.care4u.toolbox.sheet.rental.rental_sheet;

import java.time.LocalDateTime;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalSheetDto {
	
	private long id;
	
	private MembershipDto workerDto;
	
	private MembershipDto leaderDto;
	
	private MembershipDto approverDto;
	
	private ToolboxDto toolboxDto;
	
	private LocalDateTime eventTimestamp;
	
	@Builder
	public RentalSheetDto(long id, Membership worker, Membership leader, Membership approver, Toolbox toolbox, LocalDateTime eventTimestamp) {
		this.id = id;
		this.workerDto = new MembershipDto(worker);
		this.leaderDto = new MembershipDto(leader);
		this.approverDto = new MembershipDto(approver);
		this.toolboxDto = new ToolboxDto(toolbox);
		this.eventTimestamp = eventTimestamp;
	}
	
	public RentalSheetDto(RentalSheet rentalSheet) {
		this.id = rentalSheet.getId();
		this.workerDto = new MembershipDto(rentalSheet.getWorker());
		this.leaderDto = new MembershipDto(rentalSheet.getLeader());
		this.approverDto = new MembershipDto(rentalSheet.getApprover());
		this.toolboxDto = new ToolboxDto(rentalSheet.getToolbox());
		this.eventTimestamp = rentalSheet.getEventTimestamp();
	}
	
}
