package com.care4u.toolbox.sheet.rental.rental_request_sheet;

import java.time.LocalDateTime;
import java.util.List;

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
public class RentalRequestSheetFormDto {
	
	private String workName;
	@NotNull(message="작업자 정보는 필수 입력값입니다.")
	@Min(value=1,message="작업자 정보는 필수 입력값입니다.")
	private Long workerDtoId;
	@NotNull(message="리더 정보는 필수 입력값입니다.")
	@Min(value=1,message="리더 정보는 필수 입력값입니다.")
	private Long leaderDtoId;
	@NotNull(message="정비실 정보는 필수 입력값입니다.")
	@Min(value=1,message="정비실 정보는 필수 입력값입니다.")
	private Long toolboxDtoId;
	@NotEmpty(message="공기구 목록은 필수 입력값입니다.")
	private List<RentalRequestToolFormDto> toolList;
	
}
