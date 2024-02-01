package com.care4u.toolbox;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.care4u.hr.membership.MembershipDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class ToolboxFormDto {

	@NotNull(message="아이디는 필수 입력 값입니다.")
	private Long id;
	
	@NotBlank(message="이름은 필수 입력 값입니다.")
	private String name;
	
	@NotBlank(message="정비실장은 필수 입력 값입니다.")
	private String managerDtoCode;
		
}