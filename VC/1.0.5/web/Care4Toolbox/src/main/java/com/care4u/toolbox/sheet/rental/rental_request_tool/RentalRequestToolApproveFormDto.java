package com.care4u.toolbox.sheet.rental.rental_request_tool;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RentalRequestToolApproveFormDto {
	@NotNull
	private Long id; 
	@NotNull
	private Long toolDtoId;
	@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int count;
	private String tags;
}
