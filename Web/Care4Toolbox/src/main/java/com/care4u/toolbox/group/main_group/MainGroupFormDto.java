package com.care4u.toolbox.group.main_group;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString

public class MainGroupFormDto {

	@NotNull(message="아이디는 필수 입력 값입니다.")
	private Long id;
	
	@NotBlank(message="이름은 필수 입력 값입니다.")
	private String name;
}