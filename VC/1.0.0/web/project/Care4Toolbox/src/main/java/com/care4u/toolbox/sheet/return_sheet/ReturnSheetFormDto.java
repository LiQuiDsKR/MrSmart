package com.care4u.toolbox.sheet.return_sheet;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.care4u.constant.SheetState;
import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipDto;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.ToolboxDto;
import com.care4u.toolbox.sheet.rental.rental_request_tool.RentalRequestToolFormDto;
import com.care4u.toolbox.sheet.return_tool.ReturnToolFormDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class ReturnSheetFormDto {
	@NotNull
	private Long rentalSheetDtoId;
	@NotNull(message="작업자 정보는 필수 입력값입니다.")
	private Long workerDtoId;
	@NotNull(message="승인자 정보는 필수 입력값입니다.")
	private Long approverDtoId;
	@NotNull(message="정비실 정보는 필수 입력값입니다.")
	private Long toolboxDtoId;
	@NotEmpty(message="공기구 목록은 필수 입력값입니다.")
	private List<ReturnToolFormDto> toolList;
	
}
