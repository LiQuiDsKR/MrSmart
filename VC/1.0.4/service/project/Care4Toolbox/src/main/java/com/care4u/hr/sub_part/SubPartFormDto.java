package com.care4u.hr.sub_part;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class SubPartFormDto {
	
	@NotNull(message="아이디는 필수 입력 값입니다.")
	private Long id;
	
	@NotBlank(message="이름은 필수 입력 값입니다.")
	private String name;
	
	@NotNull(message="부서는 필수 입력 값입니다.")
    private Long mainPartDtoId;
}
