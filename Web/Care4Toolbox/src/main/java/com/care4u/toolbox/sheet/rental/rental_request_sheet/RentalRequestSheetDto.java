package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalRequestSheetDto {
	
	private long id;
	
	private MembershipDto workerDto;
	
	private MembershipDto leaderDto;
	
	private ToolboxDto toolboxDto;
	
	private SheetState status;
	
	private LocalDateTime eventTimestamp;
	
	@Builder
	public RentalRequestSheetDto(long id, Membership worker, Membership leader, Toolbox toolbox, SheetState status, LocalDateTime eventTimestamp) {
		this.id = id;
		this.workerDto = new MembershipDto(worker);
		this.leaderDto = new MembershipDto(leader);
		this.toolboxDto = new ToolboxDto(toolbox);
		this.status = status;
		this.eventTimestamp = eventTimestamp;
	}
	
	public RentalRequestSheetDto(RentalRequestSheet rentalSheet) {
		this.id = rentalSheet.getId();
		this.workerDto = new MembershipDto(rentalSheet.getWorker());
		this.leaderDto = new MembershipDto(rentalSheet.getLeader());
		this.toolboxDto = new ToolboxDto(rentalSheet.getToolbox());
		this.status = rentalSheet.getStatus();
		this.eventTimestamp = rentalSheet.getEventTimestamp();
	}
	
}
