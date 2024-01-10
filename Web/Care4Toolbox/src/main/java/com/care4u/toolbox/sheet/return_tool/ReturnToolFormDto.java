package com.care4u.toolbox.sheet.return_tool;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.care4u.constant.ToolState;
import com.care4u.toolbox.tool.Tool;
import com.care4u.toolbox.tool.ToolDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReturnToolFormDto {
	
	@NotNull
	private Long rentalToolDtoId;
	@NotNull
	private Long toolDtoId;
	@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int goodCount;
	@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int faultCount;
	@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int damageCount;
	@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int lossCount;
	private String tags;
	private String comment;
}
