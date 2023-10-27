package com.care4u.toolbox.sheet.buy_sheet;

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
public class BuySheetDto {
	
	private long id;
	
	private MembershipDto approverDto;
	
	private ToolboxDto toolboxDto;
	
	private LocalDateTime eventTimestamp;
	
	@Builder
	public BuySheetDto(long id, Membership approver, Toolbox toolboxDto, LocalDateTime eventTimestamp) {
		this.id = id;
		this.approverDto = new MembershipDto(approver);
		this.toolboxDto = new ToolboxDto(toolboxDto);
		this.eventTimestamp = eventTimestamp;
	}
	
	public BuySheetDto(BuySheet buySheet) {
		this.id = buySheet.getId();
		this.approverDto = new MembershipDto(buySheet.getApprover());
		this.toolboxDto = new ToolboxDto(buySheet.getToolbox());
		this.eventTimestamp = buySheet.getEventTimestamp();
	}
	
}
