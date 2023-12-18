package com.care4u.toolbox.sheet.buy_sheet;

import java.time.LocalDateTime;
import java.util.List;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.buy_tool.BuyToolDto;

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
	
	private List<BuyToolDto> toolList;
	
	@Builder
	public BuySheetDto(long id, Membership approver, Toolbox toolboxDto, LocalDateTime eventTimestamp,List<BuyToolDto> toolList) {
		this.id = id;
		this.approverDto = new MembershipDto(approver);
		this.toolboxDto = new ToolboxDto(toolboxDto);
		this.eventTimestamp = eventTimestamp;
		this.toolList = toolList;
	}
	
	public BuySheetDto(BuySheet buySheet, List<BuyToolDto> toolList) {
		this.id = buySheet.getId();
		this.approverDto = new MembershipDto(buySheet.getApprover());
		this.toolboxDto = new ToolboxDto(buySheet.getToolbox());
		this.eventTimestamp = buySheet.getEventTimestamp();
		this.toolList = toolList;
	}
	
}
