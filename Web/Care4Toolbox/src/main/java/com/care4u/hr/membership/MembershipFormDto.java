package com.care4u.hr.membership;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import com.care4u.constant.EmploymentState;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter @Setter @Builder
public class MembershipFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "사원번호는 필수 입력 값입니다.")
    private String code;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=0, max=16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요")
    private String password;
    
    
    /**
     * 2023-10-27 박경수 추가
     */
    @NotEmpty(message = "부서는 필수 입력 값입니다.")
    private String partDtoId;
    
    //private EmploymentState employmentState;

}