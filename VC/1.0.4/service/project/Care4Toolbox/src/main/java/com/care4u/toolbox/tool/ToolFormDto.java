package com.care4u.toolbox.tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import com.care4u.constant.EmploymentState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class ToolFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "품목 번호는 필수 입력 값입니다.")
    private String code;

    @NotEmpty(message = "구매 번호는 필수 입력 값입니다.")
    private String buyCode;
    
    private String engName;
    
    @NotNull(message = "분류는 필수 입력 값입니다.")
    private Long subGroupDtoId;
    
    private String spec;
    
    private String unit;
    
    private int price;
    
    private int replacementCycle; 

}