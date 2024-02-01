package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class RentalRequestSheetFormWithTimestampDto {
	
	@Valid
	private RentalRequestSheetFormDto sheet;
	@NotNull
	private String timestamp;
	
	
	public RentalRequestSheetFormWithTimestampDto(RentalRequestSheetFormDto sheet, String timestamp) {
		super();
		this.sheet = sheet;
		this.timestamp = timestamp;
	}
}
