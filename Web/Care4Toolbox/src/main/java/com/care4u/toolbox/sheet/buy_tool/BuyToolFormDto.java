package com.care4u.toolbox.sheet.buy_tool;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import groovy.transform.ToString;
import lombok.Getter;

@Getter
@ToString
public class BuyToolFormDto {
	@NotNull
	private Long toolDtoId;
	//@Min(value=1, message="수량은 1 이상이어야 합니다")
	private int count;
}
