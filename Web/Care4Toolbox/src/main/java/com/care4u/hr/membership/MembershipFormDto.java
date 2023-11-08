package com.care4u.hr.membership;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import com.care4u.constant.EmploymentState;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class MembershipFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "사원번호는 필수 입력 값입니다.")
    private String code;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=0, max=16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요")
    private String password;
    
    @NotNull(message = "부서는 필수 입력 값입니다.")
    private Long partDtoId;
    
    @NotNull(message = "재직 상태는 필수 입력 값입니다.")
    private EmploymentState employmentStatus;

}