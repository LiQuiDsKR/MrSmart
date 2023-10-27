package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheet;
import com.care4u.toolbox.sheet.rental.rental_sheet.RentalSheetDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReturnSheetDto {
	
	private long id;
	
	private RentalSheetDto rentalSheetDto;
	
	private MembershipDto workerDto;
	
	private MembershipDto leaderDto;
	
	private MembershipDto approverDto;
	
	private ToolboxDto toolboxDto;
	
	private LocalDateTime eventTimestamp;
	
	@Builder
	public ReturnSheetDto(long id, RentalSheet rentalSheet, Membership worker, Membership approver, Toolbox toolbox, LocalDateTime eventTimestamp) {
		this.id = id;
		this.rentalSheetDto = new RentalSheetDto(rentalSheet);
		this.workerDto = new MembershipDto(worker);
		this.leaderDto = new MembershipDto(rentalSheet.getLeader());
		this.approverDto = new MembershipDto(approver);
		this.toolboxDto = new ToolboxDto(toolbox);
		this.eventTimestamp = eventTimestamp;
	}
	
	public ReturnSheetDto(ReturnSheet returnSheet) {
		this.id = returnSheet.getId();
		this.rentalSheetDto = new RentalSheetDto(returnSheet.getRentalSheet());
		this.workerDto = new MembershipDto(returnSheet.getWorker());
		this.leaderDto = new MembershipDto(returnSheet.getLeader());
		this.approverDto = new MembershipDto(returnSheet.getApprover());
		this.toolboxDto = new ToolboxDto(returnSheet.getToolbox());
		this.eventTimestamp = returnSheet.getEventTimestamp();
	}
	
}
