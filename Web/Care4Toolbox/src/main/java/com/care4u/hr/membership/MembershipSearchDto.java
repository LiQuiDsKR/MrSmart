/**
 * 2023-10-25 박경수
 * search & paging 기능 테스트를 위해 추가했습니다
 */

package com.care4u.hr.membership;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.part.PartDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MembershipSearchDto {
	
	private PartDto searchPartDto;
	
	private Role searchRole;
	
	private EmploymentState searchEmploymentState;
	
    private String searchBy; // id / name / code
	
	private String searchQuery="";
	
}
